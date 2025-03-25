package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferService skillOfferService;
    private final UserSkillGuaranteeService userSkillGuaranteeService;

    @Override
    public boolean doesSkillExists(long skillId) {
        return skillRepository.existsById(skillId);
    }

    @Override
    public List<Skill> findSkillsByUserId(long userId) {
        return skillRepository.findAllByUserId(userId);
    }

    @Override
    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.title())) {
            throw new DataValidationException("This skill already exists");
        }
        Skill skill = skillMapper.toEntity(skillDto);
        skillRepository.save(skill);
        return skillMapper.toDto(skill);
    }

    @Override
    public List<SkillDto> getUserSkills(Long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skills.stream().map(skillMapper::toDto)
                .toList();
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        Map<Long, Long> skillCount = offeredSkills.stream()
                .collect(Collectors.groupingBy(Skill::getId, Collectors.counting()));

        return skillCount.entrySet().stream()
                .map(entry -> toSkillCandidateDto(entry, offeredSkills))
                .collect(Collectors.toList());

    }

    @Override
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Optional<Skill> optionalSkill = skillRepository.findUserSkill(skillId, userId);
        if (optionalSkill.isPresent()) {
            throw new DataValidationException(String.format("This skill \"%s\" by user already exists",
                    optionalSkill.get().getTitle()));
        }
        skillOfferService.isEnoughAmountOffersToSkill(skillId, userId);

        skillRepository.assignSkillToUser(skillId, userId);
        userSkillGuaranteeService.addUserSkillGuarantee(skillId, userId);

        return skillMapper.toDto(skillRepository.findUserSkill(skillId, userId).get());
    }

    private SkillCandidateDto toSkillCandidateDto(Map.Entry<Long, Long> entry, List<Skill> offeredSkills) {
        Skill skill = findSkillById(entry.getKey(), offeredSkills)
                .orElseThrow(() -> new IllegalStateException("Skill not found for id: " + entry.getKey()));
        SkillDto skillDto = skillMapper.toDto(skill);
        return new SkillCandidateDto(skillDto, entry.getValue());
    }

    private Optional<Skill> findSkillById(Long id, List<Skill> skills) {
        return skills.stream().filter(skill -> skill.getId() == id).findFirst();
    }
}
