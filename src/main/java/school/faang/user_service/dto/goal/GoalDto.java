package school.faang.user_service.dto.goal;

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
    private String title;
    private GoalStatus status;
    private String description;
    private List<Long> skillIds;
    private LocalDateTime deadline;
}