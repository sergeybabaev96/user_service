package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.joda.time.LocalDateTime;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
public class GoalDto {
    private Long id;

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

    private LocalDateTime deadline;
}