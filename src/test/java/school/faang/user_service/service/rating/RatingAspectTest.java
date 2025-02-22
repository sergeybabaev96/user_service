package school.faang.user_service.service.rating;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.rating.UserRating;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.service.rating.annotation.RatingChanging;

import java.lang.reflect.Method;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingAspectTest {
    @Mock
    RatingService ratingService;
    @InjectMocks
    RatingAspect ratingAspect;
    UserRating mockedResult;

    @Test
    void addScore() {
        setResultSubscriptionRating();
        JoinPoint joinPoint = mock(JoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        Method method = mock(Method.class);
        RatingChanging ratingChanging = mock(RatingChanging.class);

        when(ratingService.addScore(3L, RatingType.SUBSCRIPTION_RATING)).thenReturn(mockedResult);
        when(joinPoint.getArgs()).thenReturn(new Object[]{7L, 3L});
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);
        when(method.getAnnotation(RatingChanging.class)).thenReturn(ratingChanging);
        when(ratingChanging.ratingType()).thenReturn(RatingType.SUBSCRIPTION_RATING);
        when(ratingChanging.positiveAction()).thenReturn(true);

        UserRating actualResult = ratingAspect.changeRating(joinPoint);

        Assertions.assertEquals(mockedResult, actualResult);
        verify(ratingService).addScore(3L, RatingType.SUBSCRIPTION_RATING);
        verify(joinPoint, times(1)).getArgs();
        verify(joinPoint, times(1)).getSignature();
        verify(signature, times(1)).getMethod();
        verify(method, times(1)).getAnnotation(RatingChanging.class);
        verify(ratingChanging, times(1)).ratingType();
        verify(ratingChanging, times(1)).positiveAction();
    }

    @Test
    void minusScore() {
        setResultSubscriptionRating();
        JoinPoint joinPoint = mock(JoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        Method method = mock(Method.class);
        RatingChanging ratingChanging = mock(RatingChanging.class);

        when(ratingService.minusScore(3L, RatingType.SUBSCRIPTION_RATING)).thenReturn(mockedResult);
        when(joinPoint.getArgs()).thenReturn(new Object[]{7L, 3L});
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);
        when(method.getAnnotation(RatingChanging.class)).thenReturn(ratingChanging);
        when(ratingChanging.ratingType()).thenReturn(RatingType.SUBSCRIPTION_RATING);
        when(ratingChanging.positiveAction()).thenReturn(false);

        UserRating actualResult = ratingAspect.changeRating(joinPoint);

        Assertions.assertEquals(mockedResult, actualResult);
        verify(ratingService).minusScore(3L, RatingType.SUBSCRIPTION_RATING);
        verify(joinPoint, times(1)).getArgs();
        verify(joinPoint, times(1)).getSignature();
        verify(signature, times(1)).getMethod();
        verify(method, times(1)).getAnnotation(RatingChanging.class);
        verify(ratingChanging, times(1)).ratingType();
        verify(ratingChanging, times(1)).positiveAction();
    }

    void setResultSubscriptionRating() {
        mockedResult = UserRating.builder()
                .id(1L)
                .score(20)
                .type(UserRatingType.builder()
                        .id(2L)
                        .cost(20)
                        .name(RatingType.SUBSCRIPTION_RATING)
                        .isActivity(true)
                        .build())
                .user(User.builder()
                        .id(3L)
                        .username("testUser")
                        .build())
                .build();
    }
}