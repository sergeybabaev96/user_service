package school.faang.user_service.queue;

public interface RedisPublisher<T> {
    void publish(final T message);
}
