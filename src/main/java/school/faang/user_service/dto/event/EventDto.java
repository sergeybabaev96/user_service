package school.faang.user_service.dto.event;

import java.time.LocalDateTime;
import java.util.List;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;


@Data
@Builder
public class EventDto {
    private Long id;
    @NotNull
    private String title;
    @NotNull
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @NotNull
    private Long ownerId;
    private String description;
    private List<Long> relatedSkills;
    @NotNull
    private String location;
    private int maxAttendees;
    @NotNull
    private EventType eventType;
    @NotNull
    private EventStatus eventStatus;
}
