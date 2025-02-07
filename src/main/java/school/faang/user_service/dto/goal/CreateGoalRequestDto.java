package school.faang.user_service.dto.goal;

import lombok.Data;
import jakarta.validation.constraints.*;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateGoalRequestDto {

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 64, message = "Title must be at most 64 characters")
    private String title;

    @NotNull(message = "Status cannot be null")
    private GoalStatus status;

    @NotBlank(message = "Description cannot be empty")
    @Size(max = 128, message = "Description must be at most 128 characters")
    private String description;

    private Long parentId;

    private List<Long> skillIds;

    @Future(message = "Deadline must be in the future")
    private LocalDateTime deadline;
}