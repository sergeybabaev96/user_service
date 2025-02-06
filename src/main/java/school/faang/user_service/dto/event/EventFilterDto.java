package school.faang.user_service.dto.event;

import lombok.Data;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventFilterDto {

        private String titlePattern;
        private String descriptionPattern;
        private List<EventType> eventTypes;
        private String locationPattern;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private List<EventStatus> eventStatuses;
        private Long ownerId;


}
