package work.seoeungi.jichul.domain.exchangeRate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateResponse {

    @Default
    private Double rate = 0.0;
    @Default
    private Double detail = 0.0;
    private String update;
}
