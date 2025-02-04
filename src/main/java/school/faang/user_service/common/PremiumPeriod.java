package school.faang.user_service.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import school.faang.user_service.exception.PremiumInvalidDataException;

import java.math.BigDecimal;

import static school.faang.user_service.common.Currency.USD;

@Getter
@AllArgsConstructor
public enum PremiumPeriod {
    ONE_MONTH(30, BigDecimal.valueOf(10L), USD),
    THREE_MONTHS(90, BigDecimal.valueOf(25L), USD),
    ONE_YEAR(365, BigDecimal.valueOf(80L), USD);

    private final Integer days;
    private final BigDecimal price;
    private final Currency currency;

    public static PremiumPeriod fromDays(Integer requestedDays) {
        for (PremiumPeriod premiumPeriod : values()) {
            if (premiumPeriod.days.equals(requestedDays)) {
                return premiumPeriod;
            }
        }
        throw new PremiumInvalidDataException(String.format("No PremiumPeriod by days: %d", requestedDays));
    }
}
