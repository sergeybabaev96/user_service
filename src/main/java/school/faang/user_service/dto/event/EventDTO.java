package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EventDTO {
    private Long id;
    @NotBlank
    private String title;
    @Future
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @NotNull
    private Long ownerId;
    private String description;
    private List<Long> relatedSkills;
    private String location;
    private int maxAttendees;
    private EventType eventType;
    private EventStatus eventStatus;
}
