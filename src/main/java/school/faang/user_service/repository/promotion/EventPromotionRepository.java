package school.faang.user_service.repository.promotion;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.promotion.EventPromotion;

import java.time.LocalDateTime;

public interface EventPromotionRepository extends JpaRepository<EventPromotion, Long> {
    EventPromotion findSamePromotion(Long eventId, LocalDateTime startDate, LocalDateTime endDate, Integer percentage);

    EventPromotion findPromotionByIdStartDateEndDate(Long eventId, LocalDateTime startDate, LocalDateTime endDate);

    @Modifying
    @Transactional
    @Query("UPDATE EventPromotion u SET u.percentage = :percentage WHERE u.eventId = :eventId AND u.startDate = :startDate AND u.endDate = :endDate")
    int updatePromotionPercentage(Long eventId, LocalDateTime startDate, LocalDateTime endDate, Integer percentage);

}
