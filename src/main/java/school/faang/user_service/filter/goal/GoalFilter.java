package school.faang.user_service.filter.goal;

import jakarta.validation.constraints.NotNull;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

/**
 * Интерфейс для фильтрации целей.
 * Определяет метод для проверки применимости фильтра и метод его применения к потоку целей.
 */
public interface GoalFilter {
    /**
     * Проверяет, применим ли данный фильтр к указанному DTO фильтра.
     *
     * @param filter DTO фильтра.
     * @return true, если фильтр применим, иначе false.
     */
    boolean isApplicable(@NotNull GoalFilterDto filter);

    /**
     * Применение фильтра к потоку целей
     *
     * @param goals Поток целей, которые небходимо отфильтровать
     * @param filter DTO фильтра с данными для сортировки
     * @return Отфильтрованный поток целей
     */
    Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filter);
}
