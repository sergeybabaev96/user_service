package school.faang.user_service.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SubscriptionErrorResponse {
    String message;
    private long timestamp;
}
