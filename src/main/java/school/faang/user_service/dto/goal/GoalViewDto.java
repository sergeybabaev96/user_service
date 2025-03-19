package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GoalViewDto {
    @NotNull(message = "Id cannot be null")
    private Long id;

    private Long parentId;

    @NotNull(message = "Title cannot be null")
    private String title;

    @NotNull(message = "Description cannot be null")
    private String description;

    private GoalStatus status;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long mentorId;

    @NotNull(message = "Users id cannot be null")
    private List<Long> usersId;

    @NotNull(message = "Skills to achieve id cannot be null")
    private List<Long> skillsToAchieveId;
}
