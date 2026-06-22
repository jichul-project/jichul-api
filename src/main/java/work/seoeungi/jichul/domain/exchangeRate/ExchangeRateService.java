package work.seoeungi.jichul.domain.exchangeRate;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import work.seoeungi.jichul.domain.exchangeRate.dto.ExchangeRateResponse;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final StringRedisTemplate redisTemplate;

    public ExchangeRateResponse get() {
        Object exchangeRateKrw = redisTemplate.opsForHash().get("exchangeRate", "krw");

        if (exchangeRateKrw == null) {
            return null;
        } else {
            double detail = Double.parseDouble(exchangeRateKrw.toString());
            double rate = Math.floor(detail);

            ExchangeRateResponse response = ExchangeRateResponse.builder()
                .rate(rate)
                .detail(detail)
                .build();

            Object exchangeRateUpdate = redisTemplate.opsForHash().get("exchangeRate", "update");

            if (exchangeRateUpdate != null) {
                response.setUpdate(exchangeRateUpdate.toString());
            }

            return response;
        }
    }

    public BigDecimal getSimple() {
        return BigDecimal.valueOf(this.get().getRate());
    }
}
