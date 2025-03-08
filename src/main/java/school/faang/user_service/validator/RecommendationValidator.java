package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {

    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public void validateRecommendationDate(LocalDateTime recommendationDate, LocalDateTime thresholdDate) {
        if (thresholdDate.isBefore(recommendationDate)) {
            Duration diff = Duration.between(recommendationDate, thresholdDate);
            throw new DataValidationException(" Нельзя создать рекомендацию раньше 6 месяцев с последней рекомендации пользователю прошло "
                    + diff.toDaysPart() + " д. " + diff.toHoursPart() + " ч. " + diff.toMinutesPart() + " мин.");
        }
    }

    public void validatorExistenceUserSkillGuarantee(RecommendationDto recommendation, List<Long> skillIds) {
        List<UserSkillGuarantee> userSkillGuaranteeList = userSkillGuaranteeRepository.findBySkillIdInAndGuarantorIdAndUserId(
                skillIds, recommendation.getAuthorId(), recommendation.getReceiverId());
        if (!userSkillGuaranteeList.isEmpty()) {
            String skillTitle = userSkillGuaranteeList.stream()
                    .map(userSkillGuarantee -> userSkillGuarantee.getSkill().getTitle()) // Получаем название навыка
                    .collect(Collectors.joining(", "));

            throw new DataValidationException("Вы уже являетесь гарантом навыков: " + skillTitle);
        }
    }

    public void validatorIdDuplicates(List<Long> skillId) {
        Set<Long> setId = new HashSet<>(skillId);
        if (skillId.size() != setId.size()) {
            throw new DataValidationException("В рекомендации не может быть несколько предложений одного и того же навыка!");
        }
    }
}
