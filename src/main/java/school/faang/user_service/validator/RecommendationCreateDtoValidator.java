package school.faang.user_service.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationCreateDto;
import school.faang.user_service.exception.DataValidationException;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class RecommendationCreateDtoValidator {
    private final Validator validator;

    public void validate(RecommendationCreateDto recommendationCreateDto) {
        log.debug("validate recommendationDto: {}", recommendationCreateDto);

        Set<ConstraintViolation<RecommendationCreateDto>> constraintViolations = validator.
                validate(recommendationCreateDto);

        if (constraintViolations.isEmpty()) {
            return;
        }
        StringBuilder message = new StringBuilder();

        constraintViolations.forEach(constraintViolation ->
                message.append(constraintViolation.getMessage()));

        throw new DataValidationException(message.toString());
    }
}
