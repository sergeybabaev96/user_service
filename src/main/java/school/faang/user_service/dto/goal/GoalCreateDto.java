package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GoalCreateDto {
    private Long parentId;

    @NotNull(message = "Title cannot be null")
    private String title;

    private String description;
    private GoalStatus status = GoalStatus.ACTIVE;
    private LocalDateTime deadline;
    private List<Long> skillsToAchieveId;
}
