package school.faang.user_service.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.dto.skill.SkillDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.SkillService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillMapper skillMapper;
    private static final int MIN_SKILL_OFFERS = 3;

    @Override
    public SkillDto create(SkillDto skillDto) {
        validateSkill(skillDto);
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Skill with title '" + skillDto.getTitle() + "' already exists.");
        }
        Skill skill = skillMapper.toEntity(skillDto);
        Skill savedSkill = skillRepository.save(skill);

        return skillMapper.toDto(savedSkill);
    }

    @Override
    public List<SkillDto> getUserSkills(Long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skills.stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);

        Map<Skill, Long> skillCountMap = skills.stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()));

        return skillCountMap.entrySet().stream()
                .map(entry -> skillMapper.toSkillCandidateDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public SkillDto acquireSkillFromOffers(Long skillId, Long userId) {
        // Проверяем, есть ли у пользователя уже этот скилл
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("User already has this skill");
        }

        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (offers.size() < MIN_SKILL_OFFERS) {
            throw new DataValidationException("Not enough skill offers to acquire this skill.");
        }

        skillRepository.assignSkillToUser(skillId, userId);

        List<UserSkillGuarantee> guarantees = offers.stream()
                .map(offer -> new UserSkillGuarantee(null, offer.getRecommendation().getReceiver(),
                        offer.getSkill(), offer.getRecommendation().getAuthor()))
                .collect(Collectors.toList());

        userSkillGuaranteeRepository.saveAll(guarantees);

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException("Skill not found"));
        return skillMapper.toDto(skill);
    }

    private void validateSkill(SkillDto skillDto) {
        if (Objects.isNull(skillDto) || Objects.isNull(skillDto.getTitle()) || skillDto.getTitle().trim().isEmpty()) {
            throw new DataValidationException("Skill title is empty");
        }
    }
}
