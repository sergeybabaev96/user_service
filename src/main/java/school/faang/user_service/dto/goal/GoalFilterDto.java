package school.faang.user_service.dto.goal;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GoalFilterDto {
    private String titlePattern;
    private String descriptionPattern;
}
