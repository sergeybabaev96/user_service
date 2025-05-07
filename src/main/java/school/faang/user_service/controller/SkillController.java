package school.faang.user_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/skill")
public class SkillController {
    private final SkillService skillService;

    @PostMapping("/create")
    public SkillDto create(@RequestBody SkillDto skill) {
        if (!validateSkill(skill)) {
            throw new DataValidationException("Invalid skill title.");
        }
        return skillService.create(skill);
    }

    @GetMapping("/get")
    public List<SkillDto> getUserSkills(@RequestParam long userId) {
        if (userId < 0) {
            throw new DataValidationException("Invalid user id.");
        }
        return skillService.getUserSkill(userId);
    }

    @GetMapping("/get/allOfferedToUser")
    public List<SkillCandidateDto> getOfferedSkills(@RequestParam long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @GetMapping("")
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    public boolean validateSkill(SkillDto skill) {
        if (skill == null || skill.getTitle().isEmpty() || skill.getTitle().isBlank()) {
            throw new IllegalArgumentException("Invalid skill title");
        }
        return true;
    }
}
