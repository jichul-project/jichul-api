package work.seoeungi.jichul.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import work.seoeungi.jichul.common.exception.AppException;
import work.seoeungi.jichul.common.exception.ErrorCode;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.access-token-expiry}")
    private long accessTokenExpiry;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;

    private SecretKey secretKey;

    private final StringRedisTemplate redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UUID userId, String email) {
        Date now = new Date();
        return Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("type", "access")
            .issuedAt(now)
            .expiration(new Date(now.getTime() + accessTokenExpiry))
            .signWith(secretKey)
            .compact();
    }

    public String generateRefreshToken(UUID userId) {
        String tokenId = UUID.randomUUID().toString();
        Date now = new Date();
        String token = Jwts.builder()
            .subject(userId.toString())
            .claim("type", "refresh")
            .claim("jti", tokenId)
            .issuedAt(now)
            .expiration(new Date(now.getTime() + refreshTokenExpiry))
            .signWith(secretKey)
            .compact();

        // Redis 에 저장 (key: refresh:{userId}:{jti})
        String key = REFRESH_TOKEN_PREFIX + userId + ":" + tokenId;
        redisTemplate.opsForValue().set(key, token, refreshTokenExpiry, TimeUnit.MILLISECONDS);

        return token;
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (ExpiredJwtException e) {
            throw new AppException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parseClaims(token).getSubject());
    }

    /**
     * 액세스 토큰 블랙리스트 등록 (로그아웃)
     */
    public void blacklistAccessToken(String token) {
        Claims claims = parseClaims(token);
        long remaining = claims.getExpiration().getTime() - System.currentTimeMillis();
        if (remaining > 0) {
            redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + token, "1", remaining, TimeUnit.MILLISECONDS
            );
        }
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(BLACKLIST_PREFIX + token);
    }

    /**
     * 리프레시 토큰 검증 및 사용 (One-time use 방식)
     */
    public UUID validateRefreshTokenAndGetUserId(String token) {
        Claims claims = parseClaims(token);

        if (!"refresh".equals(claims.get("type", String.class))) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        String userId = claims.getSubject();
        String jti = claims.get("jti", String.class);
        String key = REFRESH_TOKEN_PREFIX + userId + ":" + jti;

        String stored = redisTemplate.opsForValue().get(key);
        if (stored == null) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // 사용된 토큰 삭제 (Refresh Token Rotation)
        redisTemplate.delete(key);

        return UUID.fromString(userId);
    }

    /**
     * 해당 유저의 모든 리프레시 토큰 삭제 (로그아웃)
     */
    public void deleteAllRefreshTokens(UUID userId) {
        String pattern = REFRESH_TOKEN_PREFIX + userId + ":*";
        var keys = redisTemplate.keys(pattern);
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
