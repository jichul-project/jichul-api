package work.seoeungi.jichul.domain.subscription.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import work.seoeungi.jichul.domain.subscription.Subscription;
import work.seoeungi.jichul.domain.subscription.SubscriptionType;

public record SubscriptionResponse(
    UUID id,
    String name,
    BigDecimal amount,
    SubscriptionType type,
    UUID providerId,
    String providerName,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static SubscriptionResponse from(Subscription s) {
        return new SubscriptionResponse(
            s.getId(),
            s.getName(),
            s.getAmount(),
            s.getType(),
            s.getProvider().getId(),
            s.getProvider().getName(),
            s.getDescription(),
            s.getCreatedAt(),
            s.getUpdatedAt()
        );
    }
}
