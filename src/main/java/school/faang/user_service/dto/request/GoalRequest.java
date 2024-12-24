package school.faang.user_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.GoalDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalRequest {
    private Long userId;
    private GoalDto goal;
}
