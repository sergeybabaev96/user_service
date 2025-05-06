package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
public class GoalDto {
    private Long id;

    private String description;

    // y любой цели может быть цель-родителя
    private Long parentId;

    private String title;

    private GoalStatus status;

    // пользователь может ставить цель по определенному навыку
    private List<Long> skillIds;
}
