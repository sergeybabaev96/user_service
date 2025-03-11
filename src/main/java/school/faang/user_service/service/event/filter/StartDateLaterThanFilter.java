package school.faang.user_service.service.event.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class StartDateLaterThanFilter implements EventFilter {
    public boolean isApplicable(EventFilterDto filter) {
        return filter.startDateLaterThan() != null;
    }

    public Stream<Event> apply(EventFilterDto filter, Stream<Event> events) {
        return events
                .filter(event -> event.getStartDate().isAfter(filter.startDateLaterThan()));
    }
}
