package work.seoeungi.jichul.domain.provider.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import work.seoeungi.jichul.domain.provider.Provider;

public record ProviderResponse(
    UUID id,
    String name,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static ProviderResponse from(Provider provider) {
        return new ProviderResponse(
            provider.getId(),
            provider.getName(),
            provider.getCreatedAt(),
            provider.getUpdatedAt()
        );
    }
}
