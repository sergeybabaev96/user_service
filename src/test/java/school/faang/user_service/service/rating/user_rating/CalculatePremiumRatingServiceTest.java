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
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.premium.PremiumService;
import school.faang.user_service.service.rating.RatingTypeService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculatePremiumRatingServiceTest {
    @Mock
    UserService userService;
    @Mock
    RatingTypeService ratingTypeService;
    @Mock
    PremiumService premiumService;
    @InjectMocks
    CalculatePremiumRatingService calculatePremiumRatingService;

    @Test
    void calculate() {
        List<Long> userIds = Arrays.asList(1L, 2L);

        UserRatingType ratingType = UserRatingType.builder()
                .name(RatingType.PREMIUM_RATING)
                .cost(10)
                .build();

        when(ratingTypeService.findByName(RatingType.PREMIUM_RATING)).thenReturn(ratingType);
        when(premiumService.checkPremiumByUserId(anyLong())).thenReturn(true);

        when(userService.getUser(1L)).thenReturn(User.builder().id(1L).build());
        when(userService.getUser(2L)).thenReturn(User.builder().id(2L).build());

        List<UserRating> actualRatings = calculatePremiumRatingService.calculate(userIds);

        assertEquals(2, actualRatings.size());
        assertEquals(1, actualRatings.get(0).getUser().getId());
        assertEquals(2, actualRatings.get(1).getUser().getId());
        assertEquals(10, actualRatings.get(0).getScore());
        assertEquals(10, actualRatings.get(1).getScore());
        assertEquals(RatingType.PREMIUM_RATING, actualRatings.get(0).getType().getName());
        assertEquals(RatingType.PREMIUM_RATING, actualRatings.get(1).getType().getName());

        verify(ratingTypeService, times(1)).findByName(RatingType.PREMIUM_RATING);
        verify(premiumService, times(2)).checkPremiumByUserId(anyLong());
        verify(userService, times(2)).getUser(anyLong());
    }

    @Test
    void calculateNotHavePremium() {
        List<Long> userIds = Arrays.asList(1L, 2L);

        UserRatingType ratingType = UserRatingType.builder()
                .name(RatingType.PREMIUM_RATING)
                .cost(10)
                .build();

        when(ratingTypeService.findByName(RatingType.PREMIUM_RATING)).thenReturn(ratingType);
        when(premiumService.checkPremiumByUserId(anyLong())).thenReturn(false);

        when(userService.getUser(1L)).thenReturn(User.builder().id(1L).build());
        when(userService.getUser(2L)).thenReturn(User.builder().id(2L).build());

        List<UserRating> actualRatings = calculatePremiumRatingService.calculate(userIds);

        assertEquals(2, actualRatings.size());
        assertEquals(1, actualRatings.get(0).getUser().getId());
        assertEquals(2, actualRatings.get(1).getUser().getId());
        assertEquals(0, actualRatings.get(0).getScore());
        assertEquals(0, actualRatings.get(1).getScore());
        assertEquals(RatingType.PREMIUM_RATING, actualRatings.get(0).getType().getName());
        assertEquals(RatingType.PREMIUM_RATING, actualRatings.get(1).getType().getName());

        verify(ratingTypeService, times(1)).findByName(RatingType.PREMIUM_RATING);
        verify(premiumService, times(2)).checkPremiumByUserId(anyLong());
        verify(userService, times(2)).getUser(anyLong());
    }

    @Test
    void calculateUserIdsIsEmpty() {
        assertEquals(List.of(), calculatePremiumRatingService.calculate(List.of()));
    }
}