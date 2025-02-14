package school.faang.user_service.service.rating.user_rating;

import school.faang.user_service.entity.rating.UserRating;

import java.util.List;

public interface CalculateRatingService {
    List<UserRating> calculate(List<Long> userIds);
}
