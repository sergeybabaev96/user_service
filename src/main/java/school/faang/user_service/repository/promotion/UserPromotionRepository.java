package school.faang.user_service.repository.promotion;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.promotion.user.UserPromotion;

import java.time.LocalDateTime;

public interface UserPromotionRepository extends JpaRepository<UserPromotion, Long> {
    @Query("""
            SELECT u
            FROM UserPromotion u
            WHERE u.userId = :userId
                AND u.startDate = :startDate
                AND u.endDate = :endDate
                AND u.percentage = :percentage
                AND u.feedRank = :feedRank
            """)
    UserPromotion findSamePromotion(Long userId, LocalDateTime startDate, LocalDateTime endDate, Integer percentage, Integer feedRank);

    @Query("""
            SELECT u
            FROM UserPromotion u
            WHERE u.userId = :userId
                AND u.startDate = :startDate
                AND u.endDate = :endDate
            """)
    UserPromotion findPromotionByUserIdStartDateEndDate(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    @Modifying
    @Transactional
    @Query("""
            UPDATE UserPromotion u
            SET u.percentage = :percentage
            WHERE u.userId = :userId
                AND u.startDate = :startDate
                AND u.endDate = :endDate
            """)
    void updatePromotionPercentage(Long userId, LocalDateTime startDate, LocalDateTime endDate, Integer percentage);

}
