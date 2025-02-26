package school.faang.user_service.queue;

import school.faang.user_service.dto.RecommendationEvent;

public interface RecommendationEventPublisher {
    void publish(final RecommendationEvent message);
}
