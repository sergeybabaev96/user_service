package school.faang.user_service.service.rating;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.rating.UserRating;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.repository.rating.TopUserCacheRepository;
import school.faang.user_service.repository.rating.UserRatingRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.rating.user_rating.CalculateRatingService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Validated
@Slf4j
@RequiredArgsConstructor
@Service
public class RatingService {

    private final UserRatingRepository userRatingRepository;
    private final RatingTypeService ratingTypeService;
    private final UserService userService;
    private final TopUserCacheRepository topUserCacheRepository;
    private final List<CalculateRatingService> calculateRatingServices;

    public UserRating addRating(@Validated UserRating userRating) {
        log.info("Adding user rating to database");
        return userRatingRepository.save(userRating);
    }

    public void deleteRating(@Validated UserRating userRating) {
        log.info("Deleting user rating from database");
        userRatingRepository.delete(userRating);
    }

    public UserRating addScore(@Validated Long userId, RatingType type) {
        log.info("Adding score for user {} by type {}", userId, type);
        UserRating userRating = userRatingRepository.findByUserIdAndTypeName(userId, type);
        UserRatingType ratingType = ratingTypeService.findByName(type);

        if (userRating == null) {
            log.warn("Rating with userId %s and type %s not found".formatted(userId,
                    type));
            userRating = getEmptyUserRating(userId, ratingType);
        }

        userRating.setScore(userRating.getScore() + ratingType.getCost());
        updateFullScore(userId, ratingType.getCost());
        return userRatingRepository.save(userRating);
    }

    public UserRating minusScore(@Validated Long userId, RatingType type) {
        log.info("Minus score for user {} by type {}", userId, type);
        UserRating userRating = userRatingRepository.findByUserIdAndTypeName(userId, type);
        UserRatingType ratingType = ratingTypeService.findByName(type);

        if (userRating == null) {
            log.warn("Rating with userId %s and type %s not found".formatted(userId,
                    type));
            userRating = getEmptyUserRating(userId, ratingType);
        }

        userRating.setScore(userRating.getScore() - ratingType.getCost());

        if (userRating.getScore() < 0) {
            userRating.setScore(0);
        }
        updateFullScore(userId, -ratingType.getCost());
        return userRatingRepository.save(userRating);
    }

    public List<Long> getTopUsers() {
        Map<Long, Double> topUsers = topUserCacheRepository.getTopUsersWithScores();

        if (topUsers == null || topUsers.isEmpty()) {
            List<UserRating> userRatings = calculateAllRatings();
            updateFullRating(calculateFullScores(userRatings));
            topUsers = topUserCacheRepository.getTopUsersWithScores();
        }

        return topUsers.entrySet().stream()
                .sorted(Comparator.comparingDouble(longDoubleEntry -> -longDoubleEntry.getValue()))
                .map(Map.Entry::getKey)
                .toList();
    }

    private UserRating getEmptyUserRating(Long userId, UserRatingType ratingType) {
        UserRating userRating;
        User user = userService.getUser(userId);
        userRating = UserRating.builder()
                .user(user)
                .type(ratingType)
                .score(0)
                .build();
        return userRating;
    }

    private void updateFullRating(Map<Long, Integer> scoreMap) {
        log.info("Updating full rating");
        scoreMap.forEach((key, value) -> {
            log.debug("Updating user rating {}", key);
            topUserCacheRepository.save(key, value.doubleValue());
        });
    }

    private Map<Long, Integer> calculateFullScores(List<UserRating> userRatings) {
        log.info("Calculating full scores for user ratings");
        return userRatings.stream()
                .collect(Collectors.groupingBy(rating -> rating.getUser().getId(),
                        Collectors.summingInt(UserRating::getScore)));

    }

    private List<UserRating> calculateAllRatings() {
        log.info("Calculating all ratings");

        List<Long> allUsersId = userService.findAllUsers().stream()
                .map(User::getId)
                .toList();

        return calculateRatingServices.stream()
                .flatMap(userRank -> userRank.calculate(allUsersId).stream())
                .toList();

    }

    private void updateFullScore(@Validated Long userId, int changeScore) {
        int score = (int) topUserCacheRepository.getTopUserScore(userId);
        score += changeScore;
        topUserCacheRepository.save(userId, (double) score);
    }
}
