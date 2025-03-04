package school.faang.user_service.validator.goal;

import school.faang.user_service.dto.goal.GoalDto;

public class GoalDtoValidator {

    public static void validateGoalDto(GoalDto goalDto) {
        if (goalDto == null || goalDto.getTitle() == null || goalDto.getTitle().isBlank()) {
            throw new IllegalArgumentException("invalid goal dto " + goalDto);
        }
    }
}
