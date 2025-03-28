package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalDto {
    private Long id;
    private Long parentId;
    @NotBlank(message = "can't be blank or null ")
    private String title;
    @NotBlank(message = "can't be blank or null ")
    private String description;
    private GoalStatus status;
    private List<Long> skillIds;
    private LocalDateTime deadline;
}