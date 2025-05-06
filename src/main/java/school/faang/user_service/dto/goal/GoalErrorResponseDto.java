package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalErrorResponseDto {
    private String errorMsg;
}
