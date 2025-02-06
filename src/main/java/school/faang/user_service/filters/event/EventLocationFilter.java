package school.faang.user_service.filters.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

public class EventLocationFilter implements EventFilter{
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return !filters.getLocationPattern().isEmpty();
    }

    @Override
    public boolean filterEntity(EventDto event, EventFilterDto filters) {
        return event.getLocation().matches(filters.getLocationPattern());
    }
}
