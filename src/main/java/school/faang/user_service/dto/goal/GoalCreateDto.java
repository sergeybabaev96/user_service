package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO-класс для создания цели.
 * <p>
 * Используется для передачи данных о новой цели.
 * </p>
 * <p>
 * Содержит следующие поля:
 * <ul>
 *     <li>{@link #parentId Идентификатор родительской цели}</li>
 *     <li>{@link #title Название цели}</li>
 *     <li>{@link #description Описание цели}</li>
 *     <li>{@link #status Статус цели}</li>
 *     <li>{@link #deadline Дедлайн цели}</li>
 *     <li>{@link #skillsToAchieveId Идентификатор навыков,
 *     которые будут получены пользователями при выполнении цели}</li>
 * </ul>
 * </p>
 *
 * @author juzu400
 */
@Data
public class GoalCreateDto {
    private Long parentId;

    @NotNull(message = "Title cannot be null")
    private String title;

    private String description;
    private GoalStatus status = GoalStatus.ACTIVE;
    private LocalDateTime deadline;
    private List<Long> skillsToAchieveId;
}
