package school.faang.user_service.filters.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;

import school.faang.user_service.dto.event.EventFilterDto;


@Component
public class EventTitleFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return !filters.getTitlePattern().isEmpty();
    }

    @Override
    public boolean filterEntity(EventDto event, EventFilterDto filters) {
        return event.getTitle().contains(filters.getTitlePattern());
    }
}
