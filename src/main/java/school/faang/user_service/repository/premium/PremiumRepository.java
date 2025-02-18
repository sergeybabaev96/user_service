package school.faang.user_service.repository.premium;

import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PremiumRepository extends CrudRepository<Premium, Long> {

    boolean existsByUserId(long userId);

    Optional<Premium> findByUserIdAndEndDateAfter(Long userId, LocalDateTime currentTime);

    List<Premium> findAllByEndDateBefore(LocalDateTime endDate);

    Optional<Premium> findByUserIdAndStartDateAfterAndEndDateBefore(long userId, LocalDateTime startDate,
                                                                    LocalDateTime endDate);
}
