package school.faang.user_service.repository.promotion;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.promotion.event.EventPromotion;

import java.time.LocalDateTime;

public interface EventPromotionRepository extends JpaRepository<EventPromotion, Long> {
    @Query("""
            SELECT e
            FROM EventPromotion e
            WHERE e.eventId = :eventId
                AND e.startDate = :startDate
                AND e.endDate = :endDate
                AND e.percentage = :percentage
            """)
    EventPromotion findSamePromotion(Long eventId, LocalDateTime startDate, LocalDateTime endDate, Integer percentage);

    @Query("""
            SELECT e
            FROM EventPromotion e
            WHERE e.eventId = :eventId
                AND e.startDate = :startDate
                AND e.endDate = :endDate
            """)
    EventPromotion findPromotionByEventIdStartDateEndDate(Long eventId, LocalDateTime startDate, LocalDateTime endDate);

    @Modifying
    @Transactional
    @Query("""
            UPDATE EventPromotion e
            SET e.percentage = :percentage
            WHERE e.eventId = :eventId
                AND e.startDate = :startDate
                AND e.endDate = :endDate
            """)
    int updatePromotionPercentage(Long eventId, LocalDateTime startDate, LocalDateTime endDate, Integer percentage);

}
