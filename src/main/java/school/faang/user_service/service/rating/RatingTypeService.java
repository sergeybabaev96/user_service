package school.faang.user_service.service.rating;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.rating.UserRatingTypeRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RatingTypeService {
    private final UserRatingTypeRepository userRatingTypeRepository;

    public List<UserRatingType> findAll() {
        log.info("Find all user rating types");
        return userRatingTypeRepository.findAll();
    }

    public UserRatingType findByName(String name) {
        log.info("Find user rating type by name: {}", name);
        UserRatingType userRatingType = userRatingTypeRepository.findByName(name);
        if (userRatingType == null) {
            throw new DataValidationException("User rating type by name: %s - not found".formatted(name));
        }
        return userRatingType;
    }

    public UserRatingType add(UserRatingType userRatingType) {
        log.info("Add user rating type {}", userRatingType);
        if (userRatingType == null) {
            throw new DataValidationException("userRatingType is null");
        }
        return userRatingTypeRepository.save(userRatingType);
    }

    public UserRatingType updateCost(Long id, Integer cost) {
        log.info("Update user rating type cost {} for id {}", cost, id);
        if (id == null || cost == null) {
            throw new DataValidationException("id or cost is null");
        }
        UserRatingType sourceType = findById(id);
        sourceType.setCost(cost);
        log.info("Save user rating type {}", sourceType);
        return userRatingTypeRepository.save(sourceType);
    }

    public UserRatingType findById(Long id) {
        log.info("Find user rating type by id {}", id);
        if (id == null) {
            throw new DataValidationException("id is null");
        }
        return userRatingTypeRepository.findById(id).orElseThrow(() -> new DataValidationException("id not found"));
    }
}
