package school.faang.user_service.service.skill;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.user.UserSkillGuaranteeService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private static final int MIN_SKILL_OFFERS = 3;

    private final SkillOfferService skillOfferService;
    private final UserSkillGuaranteeService userSkillGuaranteeService;
    private final SkillRepository skillRepository;
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillMapper skillMapper;

    public boolean isAllSkillsExist(List<Long> skillIds) {
        return skillIds.stream().allMatch(skillRepository::existsById);
    }

    public void assignSkillToUser (long skillId, long userId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.title())) {
            throw new DataValidationException("The skill already exists : " + skillDto.title());
        }

        Skill skill = skillMapper.toEntity(skillDto);
        skill = skillRepository.save(skill);
        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skills.stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        return skillCandidateMapper.toSkillCandidateDtoList(skills);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = skillRepository.findUserSkill(skillId, userId).orElseGet(() -> {
            List<SkillOffer> skillOffers = skillOfferService.findAllOffersOfSkill(skillId, userId);

            if (skillOffers.size() >= MIN_SKILL_OFFERS) {
                skillRepository.assignSkillToUser(skillId, userId);
                Skill skillUser = skillRepository.findUserSkill(skillId, userId)
                        .orElseThrow(() -> new RuntimeException("Error adding a skill to a user"));
                skillOffers.forEach(skillOffer ->
                    userSkillGuaranteeService.save(UserSkillGuarantee.builder()
                            .user(skillOffer.getRecommendation().getReceiver())
                            .skill(skillUser)
                            .guarantor(skillOffer.getRecommendation().getAuthor())
                            .build()));
            }
            return skillRepository.findUserSkill(skillId, userId)
                    .orElseThrow(() -> new DataValidationException("Skill isn`t find"));
        });
        return skillMapper.toDto(skill);
    }

    public List<Skill> getSkillsByIds(List<Long> skillIds) {
        return skillIds.stream()
                .map(skillId -> skillRepository.findById(skillId)
                        .orElseThrow(() -> new EntityNotFoundException("skill with id " + skillId + " not exists")))
                .collect(Collectors.toList());
    }
}