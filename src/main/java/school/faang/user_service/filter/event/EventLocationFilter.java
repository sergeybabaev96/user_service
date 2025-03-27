package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventLocationFilter implements EventFilter{
    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.location() != null && !eventFilterDto.location().isBlank();
    }

    @Override
    public Stream<Event> apply(EventFilterDto eventFilterDto, Stream<Event> eventStream) {
        return eventStream
                .filter(event -> event.getLocation().equalsIgnoreCase(eventFilterDto.location()));
    }
}
