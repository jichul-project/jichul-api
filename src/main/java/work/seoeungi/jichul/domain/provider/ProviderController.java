package work.seoeungi.jichul.domain.provider;

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
import work.seoeungi.jichul.domain.provider.dto.ProviderRequest;
import work.seoeungi.jichul.domain.provider.dto.ProviderResponse;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProviderResponse>>> list(@AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = uuid(userDetails);
        return ResponseEntity.ok(ApiResponse.ok(providerService.findAll(userId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProviderResponse>> create(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid ProviderRequest request) {
        UUID userId = uuid(userDetails);
        return ResponseEntity.ok(ApiResponse.ok(providerService.create(userId, request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProviderResponse>> update(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID id,
        @RequestBody @Valid ProviderRequest request) {
        UUID userId = uuid(userDetails);
        return ResponseEntity.ok(ApiResponse.ok(providerService.update(userId, id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID id) {
        UUID userId = uuid(userDetails);
        providerService.delete(userId, id);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    private UUID uuid(UserDetails userDetails) {
        return UUID.fromString(userDetails.getUsername());
    }
}
