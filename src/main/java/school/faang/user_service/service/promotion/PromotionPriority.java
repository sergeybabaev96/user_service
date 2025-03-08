package school.faang.user_service.service.promotion;

import lombok.Getter;

@Getter
public enum PromotionPriority {
    PRIORITY_ULTRA(10),
    PRIORITY_PREMIUM(50),
    PRIORITY_HIGH(100),
    PRIORITY_MEDIUM(250),
    PRIORITY_NORMAL(500),
    PRIORITY_LOW(1000),
    PRIORITY_MINIMAL(2500);

    private final int feedRank;

    PromotionPriority(int feedRank) {
        this.feedRank = feedRank;
    }
}
