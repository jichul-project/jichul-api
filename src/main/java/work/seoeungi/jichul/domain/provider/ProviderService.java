package work.seoeungi.jichul.domain.provider;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.seoeungi.jichul.common.exception.AppException;
import work.seoeungi.jichul.common.exception.ErrorCode;
import work.seoeungi.jichul.domain.provider.dto.ProviderRequest;
import work.seoeungi.jichul.domain.provider.dto.ProviderResponse;
import work.seoeungi.jichul.domain.subscription.SubscriptionRepository;
import work.seoeungi.jichul.domain.user.User;
import work.seoeungi.jichul.domain.user.UserService;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    @Cacheable(value = "provider:all", key = "#userId")
    public List<ProviderResponse> findAll(UUID userId) {
        return providerRepository.findAllByUserIdOrderByNameAsc(userId)
            .stream()
            .map(ProviderResponse::from)
            .toList();
    }

    @Transactional
    @CacheEvict(value = "provider:all", key = "#userId")
    public ProviderResponse create(UUID userId, ProviderRequest request) {
        User user = userService.findById(userId);
        Provider provider = Provider.builder()
            .user(user)
            .name(request.name())
            .build();
        return ProviderResponse.from(providerRepository.save(provider));
    }

    @Transactional
    @CacheEvict(value = "provider:all", key = "#userId")
    public ProviderResponse update(UUID userId, UUID providerId, ProviderRequest request) {
        Provider provider = findOwnedProvider(userId, providerId);
        provider.updateName(request.name());
        return ProviderResponse.from(provider);
    }

    @Transactional
    @CacheEvict(value = "provider:all", key = "#userId")
    public void delete(UUID userId, UUID providerId) {
        Provider provider = findOwnedProvider(userId, providerId);

        if (subscriptionRepository.existsByProviderIdAndUserId(providerId, userId)) {
            throw new AppException(ErrorCode.PROVIDER_HAS_SUBSCRIPTIONS);
        }

        providerRepository.delete(provider);
    }

    @Transactional(readOnly = true)
    public Provider findOwnedProvider(UUID userId, UUID providerId) {
        return providerRepository.findByIdAndUserId(providerId, userId)
            .orElseThrow(() -> new AppException(ErrorCode.PROVIDER_NOT_FOUND));
    }
}
