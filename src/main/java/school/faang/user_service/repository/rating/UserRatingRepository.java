package school.faang.user_service.repository.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.rating.UserRating;
import school.faang.user_service.enums.RatingType;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {
    UserRating findByUserIdAndTypeName(Long userId, RatingType type);
}
