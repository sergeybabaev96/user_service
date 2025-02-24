package school.faang.user_service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import school.faang.user_service.annotation.PublishProfileViewEvent;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.event.AnalyticsProfileViewEvent;
import school.faang.user_service.event.Event;
import school.faang.user_service.publisher.AnalyticsProfileViewPublisher;
import school.faang.user_service.publisher.EventPublisher;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class ProfileViewAspect {
    private final Map<Class<? extends Event>, EventPublisher> eventPublisherMap = new HashMap<>();

    public ProfileViewAspect(AnalyticsProfileViewPublisher profileViewPublisher, UserContext userContext) {
        eventPublisherMap.put(AnalyticsProfileViewEvent.class, profileViewPublisher);
    }

    @AfterReturning(pointcut = "@annotation(publishProfileViewEvent)", returning = "result")
    public void publishEvent(JoinPoint joinPoint, PublishProfileViewEvent publishProfileViewEvent, Object result) {
        if (result == null) {
            log.info("Method returned {} null, event will not be published.", joinPoint.getSignature().getName());
            return;
        }

        Arrays.stream(publishProfileViewEvent.events())
                .forEach(eventClass -> {
                    EventPublisher publisher = eventPublisherMap.get(eventClass);
                    publisher.publishEvent(result);
                });
    }
}
