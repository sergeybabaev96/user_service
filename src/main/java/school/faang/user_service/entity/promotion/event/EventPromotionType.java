package school.faang.user_service.entity.promotion.event;

public enum EventPromotionType {
    TEN_PERCENT(10),
    TWENTY_FIVE_PERCENT(25),
    FORTY_PERCENT(40),
    FIFTY_PERCENT(50),
    SIXTY_PERCENT(60);

    private final int percentage;

    EventPromotionType(int percentage) {
        this.percentage = percentage;
    }

    public int getPercentage() {
        return percentage;
    }
}
