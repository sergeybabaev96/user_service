package school.faang.user_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PremiumPeriod {
    ONE_MONTH(30, new BigDecimal(10)),
    THREE_MONTHS(90, new BigDecimal(25)),
    ONE_YEAR(365, new BigDecimal(80));

    private final int days;
    private final BigDecimal price;

    public static PremiumPeriod fromDays(int days) {
        return Arrays.stream(PremiumPeriod.values())
                .filter(period -> period.getDays() == days)
                .findFirst()
                .orElseThrow(() -> ResourceNotFoundException.premiumPeriodNotFoundException(days));
    }
}
