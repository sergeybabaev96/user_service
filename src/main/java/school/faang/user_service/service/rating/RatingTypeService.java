package school.faang.user_service.service.rating;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.rating.UserRatingTypeRepository;

import java.util.List;

@Validated
@Slf4j
@RequiredArgsConstructor
@Service
public class RatingTypeService {
    private final UserRatingTypeRepository userRatingTypeRepository;

    @Value("${rating.cost.default:10}")
    private Integer defaultRatingCost;

    public List<UserRatingType> findAll() {
        log.info("Find all user rating types");
        return userRatingTypeRepository.findAll();
    }

    public UserRatingType findByName(@NotNull RatingType type) {
        log.info("Find user rating type by name: {}", type);
        UserRatingType userRatingType = userRatingTypeRepository.findByName(type);
        if (userRatingType == null) {
            userRatingType = UserRatingType.builder()
                    .name(type)
                    .cost(defaultRatingCost)
                    .isActivity(false)
                    .build();
            userRatingType = userRatingTypeRepository.save(userRatingType);
        }
        return userRatingType;
    }

    public UserRatingType add(@NotNull UserRatingType userRatingType) {
        log.info("Add user rating type {}", userRatingType);
        return userRatingTypeRepository.save(userRatingType);
    }

    public UserRatingType updateCost(@NotNull Long id, @NotNull Integer cost) {
        log.info("Update user rating type cost {} for id {}", cost, id);

        UserRatingType sourceType = findById(id);
        sourceType.setCost(cost);
        log.info("Save user rating type {}", sourceType);
        return userRatingTypeRepository.save(sourceType);
    }

    public UserRatingType findById(@NotNull Long id) {
        log.info("Find user rating type by id {}", id);
        return userRatingTypeRepository.findById(id).orElseThrow(() -> new DataValidationException("id not found"));
    }
}
