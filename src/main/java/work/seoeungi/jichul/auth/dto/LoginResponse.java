package work.seoeungi.jichul.auth.dto;

import java.util.UUID;

public record LoginResponse(
    UUID userId,
    String email,
    String name,
    String accessToken,
    String refreshToken
) {

}
