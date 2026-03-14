package work.seoeungi.jichul.domain.subscription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;
import work.seoeungi.jichul.domain.subscription.SubscriptionType;

public record SubscriptionRequest(
    @NotBlank(message = "서비스 이름을 입력해주세요.")
    @Size(max = 200, message = "서비스 이름은 200자 이하이어야 합니다.")
    String name,

    @NotNull(message = "금액을 입력해주세요.")
    @Positive(message = "금액은 0보다 커야 합니다.")
    BigDecimal amount,

    @NotNull(message = "결제 타입을 선택해주세요.")
    SubscriptionType type,

    @NotNull(message = "제공사를 선택해주세요.")
    UUID providerId,

    @Size(max = 500, message = "설명은 500자 이하이어야 합니다.")
    String description
) {

}
