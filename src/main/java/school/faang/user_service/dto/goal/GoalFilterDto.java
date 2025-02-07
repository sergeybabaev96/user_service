package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Size;
import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

@Data
public class GoalFilterDto {
    @Size(max = 64, message = "Title must be at most 64 characters")
    private String title;

    private GoalStatus status;

    private Long parentId;
}