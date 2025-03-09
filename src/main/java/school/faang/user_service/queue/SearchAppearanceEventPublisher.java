package school.faang.user_service.queue;

import school.faang.user_service.dto.queue.SearchAppearanceEvent;

public interface SearchAppearanceEventPublisher {

    void publish(SearchAppearanceEvent searchAppearanceEvent);
}
