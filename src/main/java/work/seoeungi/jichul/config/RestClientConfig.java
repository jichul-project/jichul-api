package work.seoeungi.jichul.config;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${third-party.open-exchange-rates.url}")
    private String exchangeApiBaseUrl;

    @Bean
    public RestClient exchangeRateRestClient(RestClient.Builder builder) {
        HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        return builder
            .requestFactory(requestFactory)
            .baseUrl(exchangeApiBaseUrl)
            .defaultHeader("Accept", "application/json")
            .build();
    }
}
