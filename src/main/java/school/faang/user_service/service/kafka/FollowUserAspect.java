package school.faang.user_service.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class FollowUserAspect {
    private final KafkaProducer producer;

    @Pointcut("execution(public void school.faang.user_service.service.SubscriptionService.followUser(long, long))")
    public void followUserMethodPointcut() {
    }

    @AfterReturning(pointcut = "followUserMethodPointcut()", returning = "result")
    public void afterFollowUserMethod(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        long followerId = (long) args[0];
        long followeeId = (long) args[1];

        producer.sendFollowUserEvent(followerId, followeeId);
        log.info("User {} subscribed to user {}", followerId, followeeId);
    }
}