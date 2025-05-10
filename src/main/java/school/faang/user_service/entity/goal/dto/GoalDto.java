package school.faang.user_service.entity.goal.dto;

import jakarta.validation.constraints.NotBlank;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

public record GoalDto(
        Long id,
        String description,
        Long parentId,
        @NotBlank(message = "Empty goal title not allowed!") String title,
        GoalStatus status,
        List<Long> skillsId
) {}