package work.seoeungi.jichul.domain.subscription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    @Query("SELECT s FROM Subscription s JOIN FETCH s.provider WHERE s.user.id = :userId ORDER BY s.createdAt DESC")
    List<Subscription> findAllByUserIdWithProvider(@Param("userId") UUID userId);

    Optional<Subscription> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByProviderIdAndUserId(UUID providerId, UUID userId);
}
