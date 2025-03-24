package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO-класс для представления информации о существующей цели.
 * <p>
 * Используется для передачи данных о цели между слоями приложения.
 * </p>
 * <p>
 * Содержит следующие поля:
 * <ul>
 *     <li>{@link #id Идентификатор цели}</li>
 *     <li>{@link #parentId Идентификатор родительской цели}</li>
 *     <li>{@link #title Название цели}</li>
 *     <li>{@link #description Описание цели}</li>
 *     <li>{@link #status Статус цели}</li>
 *     <li>{@link #deadline Дедлайн цели}</li>
 *     <li>{@link #createdAt Время создание цели}</li>
 *     <li>{@link #updatedAt Время последнего обновления цели}</li>
 *     <li>{@link #usersId Идентификатор пользователей с данной целью}</li>
 *     <li>{@link #skillsToAchieveId Идентификатор навыков,
 *     которые будут получены пользователями при выполнении цели}</li>
 * </ul>
 * </p>
 *
 * @author juzu400
 */
@Data
public class GoalViewDto {
    @NotNull(message = "Id cannot be null")
    private Long id;

    private Long parentId;

    @NotNull(message = "Title cannot be null")
    private String title;

    private String description;
    private GoalStatus status;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @NotNull(message = "Users id cannot be null")
    private List<Long> usersId;

    @NotNull(message = "Skills to achieve id cannot be null")
    private List<Long> skillsToAchieveId;
}
