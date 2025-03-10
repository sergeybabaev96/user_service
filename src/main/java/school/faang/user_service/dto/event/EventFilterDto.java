package school.faang.user_service.dto.event;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для фильтрации событий.
 * Содержит критерии для поиска событий, такие как название, даты и местоположение.
 *
 * @author Zhltsk-V
 */
@Data
public class EventFilterDto {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
}
