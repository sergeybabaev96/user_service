package school.faang.user_service.service.rating.user_rating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.rating.UserRating;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.premium.PremiumService;
import school.faang.user_service.service.rating.RatingTypeService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CalculatePremiumRatingService implements CalculateRatingService {

    private final UserService userService;
    private final RatingTypeService ratingTypeService;
    private final PremiumService premiumService;

    @Override
    public List<UserRating> calculate(List<Long> userIds) {
        UserRatingType ratingType = ratingTypeService.findByName(RatingType.PREMIUM_RATING);

        return userIds.stream()
                .map(userId -> {
                    int score = 0;

                    if (premiumService.checkPremiumByUserId(userId)) {
                        score = ratingType.getCost();
                    }

                    return UserRating.builder()
                            .user(userService.getUser(userId))
                            .type(ratingType)
                            .score(score)
                            .build();
                })
                .toList();
    }
}
