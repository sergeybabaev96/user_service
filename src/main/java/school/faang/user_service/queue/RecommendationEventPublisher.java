package school.faang.user_service.queue;

public interface RecommendationEventPublisher {
    void publish(final String message);
}
