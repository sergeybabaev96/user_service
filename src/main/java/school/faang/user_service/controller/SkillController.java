package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.ResponseSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@AllArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping("/skill")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseSkillDto create(@Valid @RequestBody CreateSkillDto skill) {
        return skillService.create(skill);
    }

    @GetMapping("/user/{userId}/skills")
    public List<ResponseSkillDto> getUserSkills(@PathVariable Long userId) {

        return skillService.getUserSkills(userId);
    }

    @GetMapping("/user/{userId}/offers")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable Long userId) {

        return skillService.getOfferedSkills(userId);
    }


    @PostMapping("/user/{userId}/offer/{skillId}")
    public ResponseSkillDto acquireSkillFromOffers(@PathVariable Long skillId, @PathVariable Long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
