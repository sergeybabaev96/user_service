package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillValidator {

    private final SkillRepository skillRepository;

    public void validatorTitleSkill(SkillDto skill) {
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("\" Skill \" c таким названием уже существует!");
        }
    }

    public void validatorSkillId(Long skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new DataValidationException("Skill с данным id не существует!");
        }
    }

    public void validatorExistenceSkillListSkillOffer(List<Long> skillId) {
        long result = skillRepository.countExisting(skillId);
        if (result != skillId.size()) {
            throw new DataValidationException("В списке предложенных навыков есть несуществующие навыки!");
        }
    }

    public void checkValidRecommendation(List<SkillOffer> guaranteeUser) {
        if (guaranteeUser.size() < SkillService.MIN_SKILL_OFFERS) {
            throw new DataValidationException("Слишком мало рекомендаций для получения Skill!");
        }
    }

    public void validatorSkillOfferIsEmpty(List<SkillOffer> guaranteeUser) {
        if (guaranteeUser.isEmpty()) {
            throw new DataValidationException("Пустой список рекомендаций!");
        }
    }
}