package school.faang.user_service.service.promotion;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class EventPromotionPricing {
    private static final Map<Integer, BigDecimal> prices = new HashMap<>();

    static {
        prices.put(EventPromotionType.TEN_PERCENT_OF_USERS.getEventPercentage(), new BigDecimal("300.00"));
        prices.put(EventPromotionType.TWENTY_FIVE_PERCENT_OF_USERS.getEventPercentage(), new BigDecimal("400.00"));
        prices.put(EventPromotionType.FORTY_PERCENT_OF_USERS.getEventPercentage(), new BigDecimal("500.00"));
        prices.put(EventPromotionType.FIFTY_PERCENT_OF_USERS.getEventPercentage(), new BigDecimal("600.00"));
        prices.put(EventPromotionType.SIXTY_PERCENT_OF_USERS.getEventPercentage(), new BigDecimal("700.00"));
    }

    public static BigDecimal getPrice(EventPromotionType type) {
        return prices.getOrDefault(type.getEventPercentage(), BigDecimal.ZERO);
    }
}
