package school.faang.user_service.entity.promotion.event;

import school.faang.user_service.entity.promotion.user.UserPromotionType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class EventPromotionPricing {
    private static final Map<UserPromotionType, BigDecimal> prices = new HashMap<>();

    static {
        prices.put(UserPromotionType.TEN_PERCENT, new BigDecimal("300.00"));
        prices.put(UserPromotionType.TWENTY_FIVE_PERCENT, new BigDecimal("400.00"));
        prices.put(UserPromotionType.FORTY_PERCENT, new BigDecimal("500.00"));
        prices.put(UserPromotionType.FIFTY_FIVE_PERCENT, new BigDecimal("600.00"));
        prices.put(UserPromotionType.SEVENTY_PERCENT, new BigDecimal("700.00"));
    }

    public static BigDecimal getPrice(UserPromotionType type) {
        return prices.getOrDefault(type, BigDecimal.ZERO);
    }
}
