package school.faang.user_service.dto.premium;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PremiumPlan {
    MONTH(30), QUARTER(90), YEAR(365);

    private final Integer days;

    public static PremiumPlan fromDays(int days) {
        for (PremiumPlan plan : PremiumPlan.values()) {
            if (plan.getDays().equals(days)) {
                return plan;
            }
        }
        throw new IllegalArgumentException("Нету enum со значением:  " + days);
    }
}
