package school.faang.user_service.listener;

import faang.school.event.Event;

public interface EventListener<T extends Event> {
    void listenEvent(T event);
}
