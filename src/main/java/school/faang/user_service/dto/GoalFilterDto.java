package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
public class GoalFilterDto {
    @NotNull(message = "Title cannot be null")
    private String title;

    @NotNull(message = "Description cannot be null")
    private String description;

    private GoalStatus status;

    @NotNull(message = "Skills to achieve id cannot be null")
    private List<Long> skillsToAchieve;
}
