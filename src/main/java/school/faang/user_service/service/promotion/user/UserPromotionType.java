package school.faang.user_service.service.promotion.user;

import lombok.Getter;

@Getter
public enum UserPromotionType {
    TEN_PERCENT_OF_USERS(10),
    TWENTY_FIVE_PERCENT_OF_USERS(25),
    FORTY_PERCENT_OF_USERS(40),
    FIFTY_FIVE_PERCENT_OF_USERS(55),
    SEVENTY_PERCENT_OF_USERS(70);

    private final int userPercentage;

    UserPromotionType(int userPercentage) {
        this.userPercentage = userPercentage;
    }
}
