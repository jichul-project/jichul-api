package work.seoeungi.jichul.domain.subscription;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.seoeungi.jichul.common.exception.AppException;
import work.seoeungi.jichul.common.exception.ErrorCode;
import work.seoeungi.jichul.domain.exchangeRate.ExchangeRateService;
import work.seoeungi.jichul.domain.provider.Provider;
import work.seoeungi.jichul.domain.provider.ProviderService;
import work.seoeungi.jichul.domain.subscription.dto.SubscriptionRequest;
import work.seoeungi.jichul.domain.subscription.dto.SubscriptionResponse;
import work.seoeungi.jichul.domain.subscription.dto.SummaryResponse;
import work.seoeungi.jichul.domain.user.User;
import work.seoeungi.jichul.domain.user.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ProviderService providerService;
    private final UserService userService;
    private final ExchangeRateService exchangeRateService;

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> findAll(UUID userId) {
        log.info("Subscription findAll: {}", userId);

        List<SubscriptionResponse> list = new ArrayList<>();

        List<Subscription> subscriptionList = subscriptionRepository.findAllByUserIdWithProvider(userId);

        BigDecimal exchangeRate = null;
        if (!subscriptionList.isEmpty()) {
            exchangeRate = exchangeRateService.getSimple();
        }

        for (Subscription subscription : subscriptionList) {
            SubscriptionResponse from = SubscriptionResponse.from(subscription);

            if (SubscriptionPriceType.DOLLAR.equals(from.getPriceType())) {
                BigDecimal amount = from.getAmount();

                from.setAmount(amount.multiply(exchangeRate));
                from.setBeforeAmount(amount);
            }

            list.add(from);
        }

        return list;
    }

    @Transactional(readOnly = true)
    public SummaryResponse summary(UUID userId) {
        log.info("Subscription summary: {}", userId);

        List<Subscription> list = subscriptionRepository.findAllByUserIdWithProvider(userId);

        long monthlyCount = list.stream().filter(s -> s.getType() == SubscriptionType.MONTHLY).count();
        long yearlyCount = list.stream().filter(s -> s.getType() == SubscriptionType.YEARLY).count();
        BigDecimal exchangeRate = exchangeRateService.getSimple();

        // 월결제 합산 + 년결제 월 환산
        BigDecimal monthlyTotal = BigDecimal.ZERO;
        for (Subscription subscription : list) {
            BigDecimal bigDecimal;
            if (subscription.getType() == SubscriptionType.MONTHLY) {
                bigDecimal = subscription.getAmount();

                if (SubscriptionPriceType.DOLLAR.equals(subscription.getPriceType())) {
                    bigDecimal = bigDecimal.multiply(exchangeRate);
                }
            } else {
                bigDecimal = subscription.getAmount();

                if (SubscriptionPriceType.DOLLAR.equals(subscription.getPriceType())) {
                    bigDecimal = bigDecimal.multiply(exchangeRate);
                }

                bigDecimal = bigDecimal.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
            }

            monthlyTotal = monthlyTotal.add(bigDecimal);
        }

        // 년결제 합산 + 월결제 × 12
        BigDecimal yearlyTotal = BigDecimal.ZERO;
        for (Subscription subscription : list) {
            BigDecimal bigDecimal;
            if (subscription.getType() == SubscriptionType.YEARLY) {
                bigDecimal = subscription.getAmount();

                if (SubscriptionPriceType.DOLLAR.equals(subscription.getPriceType())) {
                    bigDecimal = bigDecimal.multiply(exchangeRate);
                }
            } else {
                bigDecimal = subscription.getAmount();

                if (SubscriptionPriceType.DOLLAR.equals(subscription.getPriceType())) {
                    bigDecimal = bigDecimal.multiply(exchangeRate);
                }

                bigDecimal = bigDecimal.multiply(BigDecimal.valueOf(12));
            }
            yearlyTotal = yearlyTotal.add(bigDecimal);
        }

        return new SummaryResponse(list.size(), monthlyCount, yearlyCount, monthlyTotal, yearlyTotal);
    }

    @Transactional
    public SubscriptionResponse create(UUID userId, SubscriptionRequest request) {
        log.info("Subscription create: {}", userId);

        User user = userService.findById(userId);
        Provider provider = providerService.findOwnedProvider(userId, request.providerId());

        Subscription subscription = Subscription.builder()
            .user(user)
            .provider(provider)
            .name(request.name())
            .amount(request.amount())
            .type(request.type())
            .priceType(request.priceType())
            .description(request.description())
            .build();

        return SubscriptionResponse.from(subscriptionRepository.save(subscription));
    }

    @Transactional
    public SubscriptionResponse update(UUID userId, UUID subscriptionId, SubscriptionRequest request) {
        log.info("Subscription update: {}", userId);

        Subscription subscription = findOwned(userId, subscriptionId);
        Provider provider = providerService.findOwnedProvider(userId, request.providerId());

        subscription.update(
            request.name(),
            request.amount(),
            request.type(),
            request.priceType(),
            request.description(),
            provider
        );

        return SubscriptionResponse.from(subscription);
    }

    @Transactional
    public void delete(UUID userId, UUID subscriptionId) {
        log.info("Subscription delete: {}", userId);

        Subscription subscription = findOwned(userId, subscriptionId);
        subscriptionRepository.delete(subscription);
    }

    private Subscription findOwned(UUID userId, UUID subscriptionId) {
        log.info("Subscription findOwned: {}", userId);

        return subscriptionRepository.findByIdAndUserId(subscriptionId, userId)
            .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
    }
}
