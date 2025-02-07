package school.faang.user_service.dto.goal;

import lombok.Data;
import jakarta.validation.constraints.*;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateGoalRequestDto {

    @NotNull(message = "User ID cannot be null")
    private Long goalId;

    @Size(max = 64, message = "Title must be at most 64 characters")
    private String title;

    private GoalStatus status;

    @Size(max = 128, message = "Description must be at most 128 characters")
    private String description;

    private Long parentId;

    private List<Long> skillIds;

    @Future(message = "Deadline must be in the future")
    private LocalDateTime deadline;
}