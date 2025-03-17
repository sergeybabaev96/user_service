package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventCreateDto {
    @NotBlank(message = "Title cannot be blank")
    @NotNull(message = "Title cannot be null")
    private String title;

    @NotNull(message = "Start date cannot be null")
    private LocalDateTime startDate;

    @NotNull(message = "End date cannot be null")
    private LocalDateTime endDate;

    @NotNull(message = "Owner ID cannot be null")
    private Long ownerId;

    @NotBlank(message = "Description cannot be blank")
    @NotNull(message = "Description cannot be null")
    private String description;

    @NotNull(message = "Related skills ID list cannot be null")
    private List<Long> relatedSkillsId;

    @NotBlank(message = "Location cannot be blank")
    @NotNull(message = "Location cannot be null")
    private String location;

    @NotNull(message = "Max attendees cannot be null")
    private int maxAttendees;

    @NotNull(message = "Event type cannot be null")
    private EventType eventType;
}
