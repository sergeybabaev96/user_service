package school.faang.user_service.entity.promotion;

public enum UserPromotionType {
    TEN_PERCENT(10),
    TWENTY_FIVE_PERCENT(25),
    FORTY_PERCENT(40),
    FIFTY_FIVE_PERCENT(55),
    SEVENTY_PERCENT(70);

    private final int percentage;

    UserPromotionType(int percentage) {
        this.percentage = percentage;
    }

    public int getPercentage() {
        return percentage;
    }
}
