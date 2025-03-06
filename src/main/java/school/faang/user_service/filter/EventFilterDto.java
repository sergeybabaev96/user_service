package school.faang.user_service.filter;

import lombok.Data;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;

@Data
public class EventFilterDto {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private Integer maxAttendees;
    private Long ownerId;
    private EventType eventType;
    private EventStatus eventStatus;

    public boolean matches(Event event) {
        return (title == null || event.getTitle().toLowerCase().contains(title.toLowerCase())) &&
                (startDate == null || !event.getStartDate().isBefore(startDate)) &&
                (endDate == null || !event.getEndDate().isAfter(endDate)) &&
                (location == null || event.getLocation().equalsIgnoreCase(location)) &&
                (maxAttendees == null || event.getMaxAttendees() <= maxAttendees) &&
                (ownerId == null || event.getOwner().getId().equals(ownerId)) &&
                (eventType == null || event.getType().equals(eventType)) &&
                (eventStatus == null || event.getStatus().equals(eventStatus));
    }
}
