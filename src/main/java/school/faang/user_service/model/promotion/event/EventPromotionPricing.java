package school.faang.user_service.model.promotion.event;

import school.faang.user_service.model.promotion.PromotionPriority;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class EventPromotionPricing {
    private static final Map<Integer, BigDecimal> prices = new HashMap<>();
    private static final Map<Integer, BigDecimal> priorityMultipliers = new HashMap<>();

    static {
        prices.put(EventPromotionType.TEN_PERCENT_OF_USERS.getEventPercentage(), new BigDecimal("30.00"));
        prices.put(EventPromotionType.TWENTY_FIVE_PERCENT_OF_USERS.getEventPercentage(), new BigDecimal("40.00"));
        prices.put(EventPromotionType.FORTY_PERCENT_OF_USERS.getEventPercentage(), new BigDecimal("50.00"));
        prices.put(EventPromotionType.FIFTY_PERCENT_OF_USERS.getEventPercentage(), new BigDecimal("60.00"));
        prices.put(EventPromotionType.SIXTY_PERCENT_OF_USERS.getEventPercentage(), new BigDecimal("70.00"));

        priorityMultipliers.put(PromotionPriority.PRIORITY_MINIMAL.getFeedRank(), new BigDecimal("1.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_LOW.getFeedRank(), new BigDecimal("2.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_NORMAL.getFeedRank(), new BigDecimal("3.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_MEDIUM.getFeedRank(), new BigDecimal("5.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_HIGH.getFeedRank(), new BigDecimal("10.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_PREMIUM.getFeedRank(), new BigDecimal("36.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_ULTRA.getFeedRank(), new BigDecimal("54.0"));
    }

    public static BigDecimal getPrice(int userPercentage, int feedRank) {
        return prices.get(userPercentage).multiply(priorityMultipliers.get(feedRank));
    }
}
