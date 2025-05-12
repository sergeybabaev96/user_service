package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class EventDto {
    private Long id;

    private String title;

    @NotNull(message = "Дата начала события обязательна")
    private LocalDateTime startDate;

    @NotNull(message = "Дата окончания события обязательна")
    private LocalDateTime endDate;

    @NotNull(message = "Не указан владелец события")
    private Long ownerId;

    private List<Long> relatedSkills;

    private String location;

    @NotBlank(message = "Название события обязательно")
    private EventType eventType;

    private EventStatus eventStatus;
}
