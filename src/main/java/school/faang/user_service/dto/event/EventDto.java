package school.faang.user_service.dto.event;

import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private Long id;

    @NotNull(message = "Title can't be null")
    @NotBlank(message = "Title can't be empty")
    private String title;

    @NotNull(message = "Start date can't be empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startDate;

    @NotNull(message = "End date can't be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate;

    @NotNull(message = "Owner can't be null")
    private Long ownerId;

    @NotNull(message = "Description can't be null")
    private String description;

    @NotNull(message = "Description can't be null")
    private List<Long> relatedSkills;

    @NotNull(message = "Location can't be null")
    private String location;

    private int maxAttendees;

    @NotNull(message = "Event type can't be null")
    @Enumerated
    private EventType eventType;

    @NotNull(message = "Event status can't be null")
    @Enumerated
    private EventStatus eventStatus;
}
