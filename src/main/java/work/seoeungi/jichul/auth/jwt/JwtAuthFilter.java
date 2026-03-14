package work.seoeungi.jichul.auth.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import work.seoeungi.jichul.common.exception.AppException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
        throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null) {
            try {
                if (jwtProvider.isBlacklisted(token)) {
                    log.warn("블랙리스트 토큰 접근 시도: {}", request.getRequestURI());
                } else {
                    Claims claims = jwtProvider.parseClaims(token);

                    if (!"access".equals(claims.get("type", String.class))) {
                        log.warn("액세스 토큰이 아닌 토큰으로 접근 시도");
                    } else {
                        // DB 재조회 없이 JWT 클레임에서 userId(subject)만 꺼내어 인증 객체 구성
                        // 서명 검증이 완료된 토큰이므로 추가 DB 조회는 불필요
                        String userId = claims.getSubject();

                        var principal = User.withUsername(userId)
                            .password("")
                            .authorities(Collections.emptyList())
                            .build();

                        var authentication = new UsernamePasswordAuthenticationToken(
                            principal, null, principal.getAuthorities()
                        );
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (AppException e) {
                log.warn("JWT 인증 실패: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearer) && bearer.startsWith(BEARER_PREFIX)) {
            return bearer.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
