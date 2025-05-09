package school.faang.user_service.controller.error_resopnses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SubscriptionErrorResponse {
    private String message;
    private long timestamp;
}
