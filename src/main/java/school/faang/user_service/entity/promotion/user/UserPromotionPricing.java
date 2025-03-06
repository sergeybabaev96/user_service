package school.faang.user_service.entity.promotion.user;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class UserPromotionPricing {
    private static final Map<UserPromotionType, BigDecimal> prices = new HashMap<>();

    static {
        prices.put(UserPromotionType.TEN_PERCENT, new BigDecimal("100.00"));
        prices.put(UserPromotionType.TWENTY_FIVE_PERCENT, new BigDecimal("200.00"));
        prices.put(UserPromotionType.FORTY_PERCENT, new BigDecimal("300.00"));
        prices.put(UserPromotionType.FIFTY_FIVE_PERCENT, new BigDecimal("400.00"));
        prices.put(UserPromotionType.SEVENTY_PERCENT, new BigDecimal("500.00"));
    }

    public static BigDecimal getPrice(UserPromotionType type) {
        return prices.getOrDefault(type, BigDecimal.ZERO);
    }
}
