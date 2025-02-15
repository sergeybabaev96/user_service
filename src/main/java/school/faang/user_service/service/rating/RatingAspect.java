package school.faang.user_service.service.rating;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.rating.UserRating;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.service.rating.annotation.RatingChanging;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class RatingAspect {

    private final RatingService ratingService;

    @After("@annotation(school.faang.user_service.service.rating.annotation.RatingChanging)")
    public UserRating changeRating(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[1];

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RatingChanging ratingChanging = signature.getMethod().getAnnotation(RatingChanging.class);
        RatingType ratingType = ratingChanging.ratingType();
        boolean positiveAction = ratingChanging.positiveAction();

        if (positiveAction) {
            return ratingService.addScore(userId, ratingType);
        }
        return ratingService.minusScore(userId, ratingType);
    }
}
