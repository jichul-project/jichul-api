package work.seoeungi.jichul.domain.subscription;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.seoeungi.jichul.common.response.ApiResponse;
import work.seoeungi.jichul.domain.subscription.dto.SubscriptionRequest;
import work.seoeungi.jichul.domain.subscription.dto.SubscriptionResponse;
import work.seoeungi.jichul.domain.subscription.dto.SummaryResponse;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<SummaryResponse>> summary(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(subscriptionService.summary(uuid(userDetails))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> list(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(subscriptionService.findAll(uuid(userDetails))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionResponse>> create(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid SubscriptionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(subscriptionService.create(uuid(userDetails), request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> update(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID id,
        @RequestBody @Valid SubscriptionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(subscriptionService.update(uuid(userDetails), id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID id) {
        subscriptionService.delete(uuid(userDetails), id);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    private UUID uuid(UserDetails userDetails) {
        return UUID.fromString(userDetails.getUsername());
    }
}
