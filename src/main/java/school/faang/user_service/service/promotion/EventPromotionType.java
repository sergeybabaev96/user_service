package school.faang.user_service.service.promotion;

public enum EventPromotionType {
    TEN_PERCENT_OF_USERS(10),
    TWENTY_FIVE_PERCENT_OF_USERS(25),
    FORTY_PERCENT_OF_USERS(40),
    FIFTY_PERCENT_OF_USERS(50),
    SIXTY_PERCENT_OF_USERS(60);

    private final int userPercentage;

    EventPromotionType(int userPercentage) {
        this.userPercentage = userPercentage;
    }

    public int getEventPercentage() {
        return userPercentage;
    }
}
