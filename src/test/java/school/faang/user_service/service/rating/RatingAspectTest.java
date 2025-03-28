package school.faang.user_service.service.rating;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.service.rating.annotation.RatingChanging;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Disabled
@ExtendWith(MockitoExtension.class)
class RatingAspectTest {

    @Mock
    private UserContext userContext;

    @Mock
    private RatingService ratingService;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private Method method;

    @Mock
    private RatingChangingForTest ratingAnnotation;

    @InjectMocks
    private RatingAspect ratingAspect;

    private abstract class RatingChangingForTest implements RatingChanging{} //this is a workaround for mocking RatingChanging annotation

    /**Args that ratingAspect will use to determine affectedUsers.<br/>If no RatingType is specified, by default userContext is used*/
    private static Map<RatingType, Object[]> argsPerRatingsMap;

    /**Users actually affected by a positive {@link RatingChanging}. Used to verify the call to {@link RatingService}.
     * If a ratingType is not specified, by default user contained in userContext will be used*/
    private static Map<RatingType, Long[]> affectedUsersPerRatingsMap;

    /**Users actually affected by a negative {@link RatingChanging}. Used to verify the call to {@link RatingService}.
     * If a ratingType is not specified, by default user contained in affectedUsersPerRatingsMap will be used for verification.
     * If that is not specified either, by default user contained in userContext will be used*/
    private static Map<RatingType, Long[]> affectedUsersPerNegativeRatingsMap;

    private static Recommendation recommendation;
    private static User userA = User.builder().id(1L).build();
    private static User userB = User.builder().id(2L).build();

    @BeforeEach
    void setUp() {
        //MockitoAnnotations.openMocks(this);
        when(userContext.getUserId()).thenReturn(userA.getId());
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(method.getAnnotation(RatingChanging.class)).thenReturn(ratingAnnotation);

        recommendation = Recommendation.builder().author(userA).receiver(userB).build();

        argsPerRatingsMap = Map.ofEntries(
                Map.entry(RatingType.RECOMMENDATION_RATING, new Object[]{recommendation}),
                Map.entry(RatingType.SUBSCRIPTION_RATING, new Long[]{userA.getId(), userB.getId()}),
                Map.entry(RatingType.SKILL_RATING, new Long[]{userB.getId()})
        );
        affectedUsersPerRatingsMap = Map.ofEntries(
                Map.entry(RatingType.RECOMMENDATION_RATING, new Long[]{userA.getId(), userB.getId()}),
                Map.entry(RatingType.SUBSCRIPTION_RATING, new Long[]{userA.getId(), userB.getId()}),
                Map.entry(RatingType.SKILL_RATING, new Long[]{userB.getId()})
        );
        affectedUsersPerNegativeRatingsMap = Map.ofEntries(
                Map.entry(RatingType.SUBSCRIPTION_RATING, new Long[]{userB.getId()})
        );
    }

    @Test
    void testChangeRating() {
        Long[] userIds;
        List<Boolean> booleans = List.of(Boolean.TRUE, Boolean.FALSE);

        //go through each rating type and test it
        for (RatingType ratingType : RatingType.values()) {

            when(ratingAnnotation.ratingType()).thenReturn(ratingType);
            when(joinPoint.getArgs()).thenReturn(argsPerRatingsMap.get(ratingType));

            for (Boolean positiveAction : booleans) {
                userIds = new Long[]{userContext.getUserId()};
                when(ratingAnnotation.positiveAction()).thenReturn(positiveAction);

                ratingAspect.changeRating(joinPoint);

                if (affectedUsersPerRatingsMap.containsKey(ratingType)) {
                    userIds = affectedUsersPerRatingsMap.get(ratingType);
                }

                if (positiveAction) {
                    verify(ratingService, times(1)).addScore(ratingType, userIds);
                } else {
                    if (affectedUsersPerNegativeRatingsMap.containsKey(ratingType)) {
                        userIds = affectedUsersPerNegativeRatingsMap.get(ratingType);
                    }
                    verify(ratingService, times(1)).minusScore(ratingType, userIds);
                }

            }

        }
    }
}