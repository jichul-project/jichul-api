package work.seoeungi.jichul.domain.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import work.seoeungi.jichul.domain.user.User;

public record UserResponse(
    UUID id,
    String email,
    String name,
    LocalDateTime createdAt
) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getName(), user.getCreatedAt());
    }
}
