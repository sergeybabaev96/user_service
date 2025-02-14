package school.faang.user_service.queue;

public interface MessagePublisher {
    void publish(final String message);
}
