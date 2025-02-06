package school.faang.user_service.filters.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.Objects;

public class EventOwnerFilter implements EventFilter{
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getOwnerId() != null;
    }

    @Override
    public boolean filterEntity(EventDto event, EventFilterDto filters) {
       return Objects.equals(event.getOwnerId(), filters.getOwnerId());
    }
}
