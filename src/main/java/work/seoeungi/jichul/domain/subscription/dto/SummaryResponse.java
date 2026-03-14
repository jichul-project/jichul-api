package work.seoeungi.jichul.domain.subscription.dto;

import java.math.BigDecimal;

public record SummaryResponse(
    long totalCount,
    long monthlyCount,
    long yearlyCount,
    BigDecimal monthlyTotal,
    BigDecimal yearlyTotal
) {

}
