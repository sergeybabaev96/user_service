package school.faang.user_service.service.rating.user_rating;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.rating.UserRating;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.rating.RatingTypeService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateSubscriptionRatingServiceTest {
    @Mock
    UserService userService;
    @Mock
    RatingTypeService ratingTypeService;
    @Mock
    SubscriptionService subscriptionService;
    @InjectMocks
    CalculateSubscriptionRatingService calculateSubscriptionRatingService;

    @Test
    void calculate() {
        List<Long> userIds = Arrays.asList(1L, 2L);

        UserRatingType ratingType = UserRatingType.builder()
                .name(RatingType.SUBSCRIPTION_RATING)
                .cost(10)
                .build();

        when(ratingTypeService.findByName(RatingType.SUBSCRIPTION_RATING)).thenReturn(ratingType);

        when(subscriptionService.getFollowersCount(1L)).thenReturn(1);
        when(subscriptionService.getFollowersCount(2L)).thenReturn(2);

        when(userService.getUser(1L)).thenReturn(User.builder().id(1L).build());
        when(userService.getUser(2L)).thenReturn(User.builder().id(2L).build());

        List<UserRating> actualRatings = calculateSubscriptionRatingService.calculate(userIds);

        assertEquals(2, actualRatings.size());
        assertEquals(1, actualRatings.get(0).getUser().getId());
        assertEquals(2, actualRatings.get(1).getUser().getId());
        assertEquals(10, actualRatings.get(0).getScore());
        assertEquals(20, actualRatings.get(1).getScore());
        assertEquals(RatingType.SUBSCRIPTION_RATING, actualRatings.get(0).getType().getName());
        assertEquals(RatingType.SUBSCRIPTION_RATING, actualRatings.get(1).getType().getName());

        verify(ratingTypeService, times(1)).findByName(RatingType.SUBSCRIPTION_RATING);
        verify(subscriptionService, times(2)).getFollowersCount(anyLong());
        verify(userService, times(2)).getUser(anyLong());
    }

    @Test
    void calculateUserIdsIsEmpty() {
        assertEquals(List.of(), calculateSubscriptionRatingService.calculate(List.of()));
    }
}