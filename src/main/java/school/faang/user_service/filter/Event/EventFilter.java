package school.faang.user_service.filter.Event;

import school.faang.user_service.entity.event.Event;

@FunctionalInterface
public interface EventFilter {
    boolean matches(Event event);
}
