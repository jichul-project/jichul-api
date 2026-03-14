package work.seoeungi.jichul.domain.subscription;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.seoeungi.jichul.common.exception.AppException;
import work.seoeungi.jichul.common.exception.ErrorCode;
import work.seoeungi.jichul.domain.provider.Provider;
import work.seoeungi.jichul.domain.provider.ProviderService;
import work.seoeungi.jichul.domain.subscription.dto.SubscriptionRequest;
import work.seoeungi.jichul.domain.subscription.dto.SubscriptionResponse;
import work.seoeungi.jichul.domain.subscription.dto.SummaryResponse;
import work.seoeungi.jichul.domain.user.UserService;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ProviderService providerService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> findAll(UUID userId) {
        return subscriptionRepository.findAllByUserIdWithProvider(userId)
            .stream()
            .map(SubscriptionResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public SummaryResponse summary(UUID userId) {
        List<Subscription> list = subscriptionRepository.findAllByUserIdWithProvider(userId);

        long monthlyCount = list.stream().filter(s -> s.getType() == SubscriptionType.MONTHLY).count();
        long yearlyCount = list.stream().filter(s -> s.getType() == SubscriptionType.YEARLY).count();

        // 월결제 합산 + 년결제 월 환산
        BigDecimal monthlyTotal = list.stream()
            .map(s -> s.getType() == SubscriptionType.MONTHLY
                ? s.getAmount()
                : s.getAmount().divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 년결제 합산 + 월결제 × 12
        BigDecimal yearlyTotal = list.stream()
            .map(s -> s.getType() == SubscriptionType.YEARLY
                ? s.getAmount()
                : s.getAmount().multiply(BigDecimal.valueOf(12)))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new SummaryResponse(list.size(), monthlyCount, yearlyCount, monthlyTotal, yearlyTotal);
    }

    @Transactional
    public SubscriptionResponse create(UUID userId, SubscriptionRequest request) {
        var user = userService.findById(userId);
        Provider provider = providerService.findOwnedProvider(userId, request.providerId());

        Subscription subscription = Subscription.builder()
            .user(user)
            .provider(provider)
            .name(request.name())
            .amount(request.amount())
            .type(request.type())
            .description(request.description())
            .build();

        return SubscriptionResponse.from(subscriptionRepository.save(subscription));
    }

    @Transactional
    public SubscriptionResponse update(UUID userId, UUID subscriptionId, SubscriptionRequest request) {
        Subscription subscription = findOwned(userId, subscriptionId);
        Provider provider = providerService.findOwnedProvider(userId, request.providerId());

        subscription.update(
            request.name(),
            request.amount(),
            request.type(),
            request.description(),
            provider
        );

        return SubscriptionResponse.from(subscription);
    }

    @Transactional
    public void delete(UUID userId, UUID subscriptionId) {
        Subscription subscription = findOwned(userId, subscriptionId);
        subscriptionRepository.delete(subscription);
    }

    private Subscription findOwned(UUID userId, UUID subscriptionId) {
        return subscriptionRepository.findByIdAndUserId(subscriptionId, userId)
            .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
    }
}
