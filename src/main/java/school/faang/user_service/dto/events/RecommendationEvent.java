package school.faang.user_service.dto.events;

import lombok.Data;

@Data
public class RecommendationEvent implements RedisEvent {
    private Long authorId;
    private Long receiverId;

    @Override
    public String getChanelEvent() {
        return "recommendationEvent";
    }
}