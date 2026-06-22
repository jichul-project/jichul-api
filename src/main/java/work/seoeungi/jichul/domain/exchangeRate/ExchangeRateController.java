package work.seoeungi.jichul.domain.exchangeRate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.seoeungi.jichul.common.response.ApiResponse;
import work.seoeungi.jichul.domain.exchangeRate.dto.ExchangeRateResponse;
import work.seoeungi.jichul.domain.subscription.dto.SummaryResponse;

@RestController
@RequestMapping("/api/exchange-rate")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping
    public ResponseEntity<ApiResponse<ExchangeRateResponse>> get(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(exchangeRateService.get()));
    }
}
