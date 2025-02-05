package school.faang.user_service.dto.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.Filter;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Component
public class EventOwnerIdFilter implements Filter<Event, EventFilterDto> {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getOwnerId() != null;
    }

    @Override
    public List<Event> apply(List<Event> events, EventFilterDto filterDto) {
        return events.stream()
                .filter(f -> filterDto.getOwnerId().equals(f.getOwner().getId()))
                .toList();
    }
}