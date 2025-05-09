package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.skill.AddedSkillNotExistException;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SkillValidator {

    public void validateExistingSkills(List<Long> absentSkillsId) {
        if (!absentSkillsId.isEmpty()) {
            String notExistingSkillsId = absentSkillsId.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw new AddedSkillNotExistException(notExistingSkillsId);
        }
    }
}