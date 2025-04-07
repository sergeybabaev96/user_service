package school.faang.user_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PremiumType {
    ONE_MONTH(10),
    THREE_MONTHS(27),
    ONE_YEAR(90);

    private int priceInDollars;
}
