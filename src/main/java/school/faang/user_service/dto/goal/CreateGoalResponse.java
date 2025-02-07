package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import org.joda.time.LocalDateTime;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
public class CreateGoalResponse {
    private Long id;
    private String title;
    private GoalStatus status;
    private String description;
    private Long parentId;
    private List<Long> skillIds;

    @PastOrPresent(message = "Update date must be in the past or present")
    private LocalDateTime createdAt;
}