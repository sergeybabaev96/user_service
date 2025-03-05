package school.faang.user_service.validator.skill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SkillValidator {
    public void validSkills(List<Long> skills, long userId) {
        if (skills == null || skills.isEmpty()) {
            log.error("Идентификаторы навыков отсутствуют или пусты для пользователя с ID {}", userId);
            throw new IllegalArgumentException("Список навыков не может быть пустым");
        }
    }
}
