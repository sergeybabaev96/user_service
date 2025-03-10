package school.faang.user_service.filter.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

/**
 * Интерфейс для фильтрации событий.
 * Определяет методы для проверки применимости фильтра и его применения к потоку событий.
 *
 * @author Zhltsk-V
 */
public interface EventFilter {
    /**
     * Проверяет, применим ли данный фильтр к указанному DTO фильтра.
     *
     * @param eventFilterDto DTO фильтра.
     * @return true, если фильтр применим, иначе false.
     */
    boolean isApplicable(EventFilterDto eventFilterDto);

    /**
     * Применяет фильтр к потоку событий.
     *
     * @param events         Поток событий.
     * @param eventFilterDto DTO фильтра.
     * @return Отфильтрованный поток событий.
     */
    Stream<Event> apply(Stream<Event> events, EventFilterDto eventFilterDto);
}
