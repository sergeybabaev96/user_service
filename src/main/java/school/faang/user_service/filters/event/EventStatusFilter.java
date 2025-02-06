package school.faang.user_service.filters.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;


public class EventStatusFilter implements EventFilter {


    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return !filters.getEventStatuses().isEmpty();
    }

    @Override
    public boolean filterEntity(EventDto event, EventFilterDto filters) {
        return filters.getEventStatuses().contains(event.getEventStatus());
    }
}
