package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {

    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public void validateRecommendationDate(LocalDateTime recommendationDate, LocalDateTime thresholdDate) {
        if (thresholdDate.isBefore(recommendationDate)) {
            Duration diff = Duration.between(thresholdDate, thresholdDate);
            throw new DataValidationException(" Нельзя создать рекомендацию раньше 6 месяцев с последней рекомендации пользователю прошло "
                    + diff.toDaysPart() + " д. " + diff.toHoursPart() + " ч. " + diff.toMinutesPart() + " мин.");
        }
    }

    public void validatorExistenceUserSkillGuarantee(RecommendationDto recommendation) {
        userSkillGuaranteeRepository.findBySkillIdAndGuarantorIdAndUserId(
                        recommendation.getSkillId(), recommendation.getAuthorId(), recommendation.getReceiverId())
                .ifPresent(guarantee -> {
                    throw new DataValidationException("Вы уже являетесь гарантом этого навыка");
                });
    }
}
