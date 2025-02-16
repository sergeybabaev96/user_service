package school.faang.user_service.filter.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import school.faang.user_service.enums.goal.GoalStatus;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoalFilterDto {

    private String title;

    private GoalStatus status;
}
