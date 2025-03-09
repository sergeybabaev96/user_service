package school.faang.user_service.model.promotion.user;

import school.faang.user_service.model.promotion.PromotionPriority;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class UserPromotionPricing {
    private static final Map<Integer, BigDecimal> prices = new HashMap<>();
    private static final Map<Integer, BigDecimal> priorityMultipliers = new HashMap<>();

    static {
        prices.put(UserPromotionType.TEN_PERCENT_OF_USERS.getUserPercentage(), new BigDecimal("10.00"));
        prices.put(UserPromotionType.TWENTY_FIVE_PERCENT_OF_USERS.getUserPercentage(), new BigDecimal("20.00"));
        prices.put(UserPromotionType.FORTY_PERCENT_OF_USERS.getUserPercentage(), new BigDecimal("30.00"));
        prices.put(UserPromotionType.FIFTY_FIVE_PERCENT_OF_USERS.getUserPercentage(), new BigDecimal("40.00"));
        prices.put(UserPromotionType.SEVENTY_PERCENT_OF_USERS.getUserPercentage(), new BigDecimal("50.00"));

        priorityMultipliers.put(PromotionPriority.PRIORITY_MINIMAL.getFeedRank(), new BigDecimal("1.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_LOW.getFeedRank(), new BigDecimal("2.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_NORMAL.getFeedRank(), new BigDecimal("4.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_MEDIUM.getFeedRank(), new BigDecimal("8.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_HIGH.getFeedRank(), new BigDecimal("16.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_PREMIUM.getFeedRank(), new BigDecimal("32.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_ULTRA.getFeedRank(), new BigDecimal("64.0"));
    }

    public static BigDecimal getPrice(int userPercentage, int feedRank) {
        return prices.get(userPercentage).multiply(priorityMultipliers.get(feedRank));
    }
}
