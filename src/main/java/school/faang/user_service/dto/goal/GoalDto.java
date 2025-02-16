package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import school.faang.user_service.enums.goal.GoalStatus;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoalDto {
    private Long id;
    private @NotBlank String title;
    private String description;
    private Long parent;
    private GoalStatus status;
    private List<Long> skillIds;
    private Long mentorId;

}
