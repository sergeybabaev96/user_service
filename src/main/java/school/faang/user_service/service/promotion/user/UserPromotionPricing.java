package school.faang.user_service.service.promotion.user;

import school.faang.user_service.service.promotion.PromotionPriority;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class UserPromotionPricing {
    private static final Map<Integer, BigDecimal> prices = new HashMap<>();
    private static final Map<Integer, BigDecimal> priorityMultipliers = new HashMap<>();

    static {
        prices.put(UserPromotionType.TEN_PERCENT_OF_USERS.getUserPercentage(), new BigDecimal("100.00"));
        prices.put(UserPromotionType.TWENTY_FIVE_PERCENT_OF_USERS.getUserPercentage(), new BigDecimal("200.00"));
        prices.put(UserPromotionType.FORTY_PERCENT_OF_USERS.getUserPercentage(), new BigDecimal("300.00"));
        prices.put(UserPromotionType.FIFTY_FIVE_PERCENT_OF_USERS.getUserPercentage(), new BigDecimal("400.00"));
        prices.put(UserPromotionType.SEVENTY_PERCENT_OF_USERS.getUserPercentage(), new BigDecimal("500.00"));

        priorityMultipliers.put(PromotionPriority.PRIORITY_MINIMAL.getFeedRank(), new BigDecimal("1.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_LOW.getFeedRank(), new BigDecimal("2.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_NORMAL.getFeedRank(), new BigDecimal("4.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_MEDIUM.getFeedRank(), new BigDecimal("8.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_HIGH.getFeedRank(), new BigDecimal("16.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_PREMIUM.getFeedRank(), new BigDecimal("32.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_ULTRA.getFeedRank(), new BigDecimal("64.0"));
    }

    public static BigDecimal getPrice(UserPromotionType type, PromotionPriority promotionPriority) {
        return prices.get(type.getUserPercentage()).multiply(priorityMultipliers.get(promotionPriority.getFeedRank()));
    }
}
