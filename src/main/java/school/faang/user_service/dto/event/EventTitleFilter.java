package school.faang.user_service.dto.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Component
public class EventTitleFilter implements EventFilter{
    @Override
    public boolean isApplicable(EventDto event) {
        return event != null && event.getTitle() != null && !event.getTitle().isEmpty();
    }

    @Override
    public List<Event> apply(List<Event> events, EventDto filter) {
        return events.stream()
                .filter(event -> event.getTitle().equals(filter.getTitle()))
                .toList();
    }
}
