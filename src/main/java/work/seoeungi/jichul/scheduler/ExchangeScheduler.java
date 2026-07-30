package work.seoeungi.jichul.scheduler;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeScheduler {

    @Value("${third-party.open-exchange-rates.key}")
    private String exchangeApiKey;

    private final StringRedisTemplate redisTemplate;
    private final RestClient exchangeRateRestClient;

    // 환율 조회
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void getExchangeRate() {
        ExchangeRateResponse response = exchangeRateRestClient.get()
            .uri(uriBuilder -> uriBuilder
                .queryParam("app_id", exchangeApiKey)
                .queryParam("base", "USD")
                .queryParam("symbols", "KRW")
                .queryParam("prettyprint", "false")
                .queryParam("show_alternative", "false")
                .build()
            )
            .retrieve()
            .body(ExchangeRateResponse.class);

        if (response != null) {
            log.info("Exchange Rate: base={}, rates={}", response.base(), response.rates());

            Double rate = response.rates().get("KRW");
            Long timestamp = response.timestamp();

            Instant instant = Instant.ofEpochSecond(timestamp);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime zdt = instant.atZone(zoneId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String update = zdt.format(formatter);

            Map<String, Object> exchangeRateMap = Map.of(
                "krw", rate.toString(),
                "update", update
            );
            redisTemplate.opsForHash().putAll("exchangeRate", exchangeRateMap);
        }
    }
}

record ExchangeRateResponse(
    String disclaimer,
    String license,
    Long timestamp,
    String base,
    Map<String, Double> rates
) {

}