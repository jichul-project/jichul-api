package work.seoeungi.jichul.domain.provider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepository extends JpaRepository<Provider, UUID> {

    List<Provider> findAllByUserIdOrderByNameAsc(UUID userId);

    Optional<Provider> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByIdAndUserId(UUID id, UUID userId);
}
