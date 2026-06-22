package work.seoeungi.jichul.domain.subscription.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import work.seoeungi.jichul.domain.subscription.Subscription;
import work.seoeungi.jichul.domain.subscription.SubscriptionPriceType;
import work.seoeungi.jichul.domain.subscription.SubscriptionType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    private UUID id;
    private String name;
    private BigDecimal amount;
    private SubscriptionType type;
    private SubscriptionPriceType priceType;
    private UUID providerId;
    private String providerName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private BigDecimal beforeAmount;

    public static SubscriptionResponse from(Subscription s) {
        return SubscriptionResponse.builder()
            .id(s.getId())
            .name(s.getName())
            .amount(s.getAmount())
            .type(s.getType())
            .priceType(s.getPriceType())
            .providerId(s.getProvider().getId())
            .providerName(s.getProvider().getName())
            .description(s.getDescription())
            .createdAt(s.getCreatedAt())
            .updatedAt(s.getUpdatedAt())
            .build();
    }
}
