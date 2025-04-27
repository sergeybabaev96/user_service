package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.publisher.FollowerEventDto;

/**
 * Компонент для публикации событий о подписках в Redis.
 * <p>
 * Отправляет события о новых подписках пользователей в Redis-канал "follower_event".
 * Используется для уведомления других сервисов о событиях подписки.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FollowerEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Публикует событие о новой подписке в Redis.
     *
     * @param event DTO-событие, содержащее информацию о подписке
     * @throws org.springframework.data.redis.RedisSystemException при ошибках работы с Redis
     */
    public void publish(FollowerEventDto event) {
        redisTemplate.convertAndSend("follower_event", event);
    }
}