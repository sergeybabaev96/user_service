package school.faang.user_service.filters.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

@Component
public class EventDateFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getStartDate() != null && filters.getEndDate() != null;
    }

    @Override
    public boolean filterEntity(EventDto event, EventFilterDto filters) {
        return filters.getStartDate().isBefore(event.getStartDate())
                && filters.getEndDate().isAfter(event.getEndDate());
    }
}
