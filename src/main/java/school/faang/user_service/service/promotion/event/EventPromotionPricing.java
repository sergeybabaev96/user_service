package school.faang.user_service.service.promotion.event;

import school.faang.user_service.service.promotion.PromotionPriority;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class EventPromotionPricing {
    private static final Map<Integer, BigDecimal> prices = new HashMap<>();
    private static final Map<Integer, BigDecimal> priorityMultipliers = new HashMap<>();

    static {
        prices.put(EventPromotionType.TEN_PERCENT_OF_USERS.getEventPercentage(), new BigDecimal("300.00"));
        prices.put(EventPromotionType.TWENTY_FIVE_PERCENT_OF_USERS.getEventPercentage(), new BigDecimal("400.00"));
        prices.put(EventPromotionType.FORTY_PERCENT_OF_USERS.getEventPercentage(), new BigDecimal("500.00"));
        prices.put(EventPromotionType.FIFTY_PERCENT_OF_USERS.getEventPercentage(), new BigDecimal("600.00"));
        prices.put(EventPromotionType.SIXTY_PERCENT_OF_USERS.getEventPercentage(), new BigDecimal("700.00"));

        priorityMultipliers.put(PromotionPriority.PRIORITY_MINIMAL.getFeedRank(), new BigDecimal("1.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_LOW.getFeedRank(), new BigDecimal("2.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_NORMAL.getFeedRank(), new BigDecimal("3.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_MEDIUM.getFeedRank(), new BigDecimal("5.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_HIGH.getFeedRank(), new BigDecimal("10.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_PREMIUM.getFeedRank(), new BigDecimal("36.0"));
        priorityMultipliers.put(PromotionPriority.PRIORITY_ULTRA.getFeedRank(), new BigDecimal("54.0"));
    }

    public static BigDecimal getPrice(EventPromotionType type, PromotionPriority promotionPriority) {
        return prices.get(type.getEventPercentage()).multiply(priorityMultipliers.get(promotionPriority.getFeedRank()));
    }
}
