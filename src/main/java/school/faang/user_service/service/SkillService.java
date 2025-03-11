package school.faang.user_service.service;

import lombok.Data;
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
@Data
public class SkillService {

    public static final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("This skill already exists");
        }
        Skill skill = skillMapper.skillDtoToSkill(skillDto);
        skillRepository.save(skill);
        return skillMapper.skillToSkillDto(skill);
    }

    public List<SkillDto> getUserSkills(Long userId) {
       List<Skill> skills = skillRepository.findAllByUserId(userId);
       List<SkillDto> skillDtos = skills.stream().map(skillMapper::skillToSkillDto)
               .toList();
       return skillDtos;
    }

    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        Map<Long,Long> skillCount = offeredSkills.stream()
                .collect(Collectors.groupingBy(Skill::getId, Collectors.counting()));
        return skillCount.entrySet().stream().map(entry -> {
            SkillDto skillDto = skillMapper.skillToSkillDto(findSkillById(entry.getKey(), offeredSkills).get());
            SkillCandidateDto  skillCandidateDto = new SkillCandidateDto();
            skillCandidateDto.setSkill(skillDto);
            skillCandidateDto.setOffersAmount(entry.getValue());
            return skillCandidateDto;
        }).toList();

    }

    public SkillDto  acquireSkillFromOffers(long skillId, long userId) {
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            return null;
        }
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        Optional<Skill> offeredSkill = findSkillById(skillId,offeredSkills);
        if (offeredSkill.isEmpty()) {
            return null;
        }
        int offers = offeredSkills.stream().filter(skill -> skill.getId() == skillId).toArray().length;
        if (offers < MIN_SKILL_OFFERS) {
            return null;
        }
        return skillMapper.skillToSkillDto(offeredSkill.get());
    }

    private Optional<Skill> findSkillById(Long id, List<Skill> skills) {
        return skills.stream().filter(skill -> skill.getId() == id).findFirst();
    }
}
