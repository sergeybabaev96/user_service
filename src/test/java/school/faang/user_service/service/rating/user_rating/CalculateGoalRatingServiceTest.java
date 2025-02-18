package school.faang.user_service.service.rating.user_rating;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.rating.UserRating;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.rating.RatingTypeService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateGoalRatingServiceTest {
    @Mock
    UserService userService;
    @Mock
    RatingTypeService ratingTypeService;
    @Mock
    GoalService goalService;
    @InjectMocks
    CalculateGoalRatingService goalRank;

    @Test
    void calculate() {
        List<Long> userIds = Arrays.asList(1L, 2L);

        UserRatingType ratingType = UserRatingType.builder()
                .name(RatingType.GOAL_RATING)
                .cost(10)
                .build();

        GoalFilterDto goalFilterDto = new GoalFilterDto();
        goalFilterDto.setStatus(GoalStatus.COMPLETED);

        when(ratingTypeService.findByName(RatingType.GOAL_RATING)).thenReturn(ratingType);

        when(goalService.getGoalsByUserId(1L, goalFilterDto)).thenReturn(List.of(Goal.builder().build()));
        when(goalService.getGoalsByUserId(2L, goalFilterDto)).thenReturn(List.of(Goal.builder().build(),
                Goal.builder().build()));

        when(userService.getUser(1L)).thenReturn(User.builder().id(1L).build());
        when(userService.getUser(2L)).thenReturn(User.builder().id(2L).build());

        List<UserRating> actualRatings = goalRank.calculate(userIds);

        assertEquals(2, actualRatings.size());
        assertEquals(1, actualRatings.get(0).getUser().getId());
        assertEquals(2, actualRatings.get(1).getUser().getId());
        assertEquals(10, actualRatings.get(0).getScore());
        assertEquals(20, actualRatings.get(1).getScore());
        assertEquals(RatingType.GOAL_RATING, actualRatings.get(0).getType().getName());
        assertEquals(RatingType.GOAL_RATING, actualRatings.get(1).getType().getName());

        verify(ratingTypeService, times(1)).findByName(RatingType.GOAL_RATING);
        verify(goalService, times(2)).getGoalsByUserId(anyLong(), any());
        verify(userService, times(2)).getUser(anyLong());
    }

    @Test
    void calculateUserIdsIsEmpty() {
        assertEquals(List.of(), goalRank.calculate(List.of()));
    }
}