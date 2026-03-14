package work.seoeungi.jichul.auth;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.seoeungi.jichul.auth.dto.LoginRequest;
import work.seoeungi.jichul.auth.dto.LoginResponse;
import work.seoeungi.jichul.auth.dto.TokenRefreshRequest;
import work.seoeungi.jichul.auth.jwt.JwtProvider;
import work.seoeungi.jichul.common.exception.AppException;
import work.seoeungi.jichul.common.exception.ErrorCode;
import work.seoeungi.jichul.domain.user.User;
import work.seoeungi.jichul.domain.user.UserService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userService.findByEmail(request.email());

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        return new LoginResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            accessToken,
            refreshToken
        );
    }

    public LoginResponse refresh(TokenRefreshRequest request) {
        UUID userId = jwtProvider.validateRefreshTokenAndGetUserId(request.refreshToken());
        User user = userService.findById(userId);

        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        return new LoginResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            accessToken,
            refreshToken
        );
    }

    public void logout(String accessToken, UUID userId) {
        jwtProvider.blacklistAccessToken(accessToken);
        jwtProvider.deleteAllRefreshTokens(userId);
    }
}
