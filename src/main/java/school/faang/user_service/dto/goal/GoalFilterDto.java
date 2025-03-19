package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalFilterDto {
    private String titlePattern;
    private String descriptionPattern;
}
