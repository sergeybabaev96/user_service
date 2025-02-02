package school.faang.user_service.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.rating.UserRating;
import school.faang.user_service.exception.RatingException;
import school.faang.user_service.service.rating.RatingService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class RatingAspect {

    private final RatingService ratingService;
//TODO:Переделать
    @Around(value = "RatingPointcuts.followUser()")
    public UserRating followUser(ProceedingJoinPoint joinPoint) {
        try {
            joinPoint.proceed();
        } catch (Throwable e) {
            throw new RatingException("FollowUser failed");
        }

        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[1];
        return ratingService.addScore(userId, "Subscription rating");
    }

    @Around(value = "RatingPointcuts.unfollowUser()")
    public UserRating unfollowUser(ProceedingJoinPoint joinPoint) {
        try {
            joinPoint.proceed();
        } catch (Throwable e) {
            throw new RatingException("UnFollowUser failed");
        }

        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[1];
        return ratingService.minusScore(userId, "Subscription rating");
    }

    @Around(value = "RatingPointcuts.acquireSkillFromOffers()")
    public UserRating acquireSkillFromOffers(ProceedingJoinPoint joinPoint) {
        try {
            joinPoint.proceed();
        } catch (Throwable e) {
            throw new RatingException("Acuire skill failed");
        }

        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[1];
        return ratingService.addScore(userId, "Skill rating");
    }

    @Around(value = "RatingPointcuts.createSkill()")
    public List<UserRating> createSkill(ProceedingJoinPoint joinPoint) {
        try {
            joinPoint.proceed();
        } catch (Throwable e) {
            throw new RatingException("Create skill failed");
        }

        Object[] args = joinPoint.getArgs();
        Skill skill = (Skill) args[0];
        return skill.getUsers().stream()
                .map(user -> ratingService.addScore(user.getId(), "Skill rating"))
                .toList();
    }
}
