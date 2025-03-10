package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillService {
    private static final String NOT_VALID_SKILL_MSG_EXCEPTION_TITLE_ALREADY_EXISTS = "Skill's title already exists";
    private static final int AMOUNT_OF_ENOUGH_CONFIRMATIONS = 3;

    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;

    public Skill create(Skill skill) {
        validateSkill(skill);
        return skillRepository.save(skill);
    }

    public List<Skill> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId);
    }

    public Map<Skill, Long> getOfferedSkills(long userId) {
        Map<Skill, Long> skillCandidateAndAmount = new HashMap<>();

        skillRepository.findSkillsOfferedToUser(userId)
                .forEach(skillCandidate ->
                        skillCandidateAndAmount.merge(skillCandidate, 1L, Long::sum));
        return skillCandidateAndAmount;
    }

    public Skill acquireSkillFromOffers(long skillId, long userId) {
        Optional<Skill> skillOpt = skillRepository.findUserSkill(skillId, userId);

        if (!skillOpt.isPresent()) {
            List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
            if (offers.size() > AMOUNT_OF_ENOUGH_CONFIRMATIONS - 1) {
                skillRepository.assignSkillToUser(skillId, userId);
            }

            skillOpt = skillRepository.findUserSkill(skillId, userId);
        }
        return skillOpt.orElse(null);
    }

    private void validateSkill(Skill skill) {
        if(skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException(NOT_VALID_SKILL_MSG_EXCEPTION_TITLE_ALREADY_EXISTS);
        }
    }
}
