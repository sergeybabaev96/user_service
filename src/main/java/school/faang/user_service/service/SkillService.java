package school.faang.user_service.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final static long MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;

    public SkillDto create(SkillDto skill) {
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("Skill already exists.");
        }
        Skill savedSkill = skillRepository.save(skillMapper.skillDtoToSkill(skill));
        return skillMapper.skillToSkillDto(savedSkill);
    }

    public List<SkillDto> getUserSkill(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skills.stream()
            .map(skillMapper::skillToSkillDto)
            .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        return skills.stream()
            .map(skillCandidateMapper::skillToSkillCandidateDto)
            .toList();
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        if (!skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("User already has this skill.");
        }

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (skillOffers.size() > MIN_SKILL_OFFERS) {
            skillRepository.assignSkillToUser(skillId, userId);
            for (SkillOffer skillOffer : skillOffers) {
                // skillOffer.getRecommendation().getAuthor()
            }
        }

        return skillMapper.skillToSkillDto(skillRepository.findById(skillId).get());
    }
}
