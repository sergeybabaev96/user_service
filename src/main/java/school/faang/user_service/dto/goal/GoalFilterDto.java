package school.faang.user_service.dto.goal;

import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

/**
 * DTO-класс с критериями фильтрации целей.
 * <p>
 * Используется для фильтрации целей.
 * </p>
 * <p>
 * Содержит следующие поля:
 * <ul>
 *     <li>{@link #title Название цели}</li>
 *     <li>{@link #description Описание цели}</li>
 *     <li>{@link #status Статус цели}</li>
 *     <li>{@link #skillsToAchieveId Идентификатор навыков,
 *     которые будут получены пользователями при выполнении цели}</li>
 * </ul>
 * </p>
 *
 * @author juzu400
 */
@Data
public class GoalFilterDto {
    private String title;
    private String description;
    private GoalStatus status;
    private List<Long> skillsToAchieveId;
}
