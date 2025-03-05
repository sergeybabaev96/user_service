package school.faang.user_service.validator.goal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;

@Slf4j
@Component
public class GoalDtoValidator {

    public void validateGoalDto(GoalDto goalDto, long userId) {
        if (goalDto == null || goalDto.getTitle() == null || goalDto.getTitle().isBlank()) {
            log.error("Title отсутствует в goal DTO для пользователя с ID {} ", userId);
            throw new IllegalArgumentException("Заголовок цели не может быть пустым");
        }
        if (goalDto.getDescription() == null || goalDto.getDescription().isBlank()) {
            log.error("Description отсутствует в goal DTO для пользователя с ID {}", userId);
            throw new IllegalArgumentException("Описание цели не может быть пустым");
        }
    }
}