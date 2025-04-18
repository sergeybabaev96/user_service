package school.faang.user_service.dto.events;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecommendationEvent implements RedisEvent {
    private Long id;
    private Long authorId;
    private Long receiverId;
    private LocalDateTime createdAt;

    @Override
    public String getChanelEvent() {
        return "recommendationEvent";
    }
}