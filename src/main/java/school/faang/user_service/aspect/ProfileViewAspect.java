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
import school.faang.user_service.service.user.UserService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class ProfileViewAspect {
    private final Map<Class<? extends Event>, EventPublisher> eventPublisherMap = new HashMap<>();

    private final UserContext userContext;
    private final UserService userService;

    public ProfileViewAspect(AnalyticsProfileViewPublisher profileViewPublisher, UserContext userContext, UserService userService) {
        this.userContext = userContext;
        this.userService = userService;
        eventPublisherMap.put(AnalyticsProfileViewEvent.class, profileViewPublisher);
    }

    @AfterReturning(pointcut = "@annotation(publishProfileViewEvent)", returning = "result")
    public void handleEvent(JoinPoint joinPoint, PublishProfileViewEvent publishProfileViewEvent, Object result) {
        if (result == null) {
            log.info("Method returned {} null, event will not be published.", joinPoint.getSignature().getName());
            return;
        }

        try {
            userService.getUserById(userContext.getUserId());
        } catch (IllegalArgumentException e) {
            log.warn("User ID is missing, events wasn't published");
            return;
        }

        if (result instanceof List) {
            List<?> results = (List<?>) result;
            results.forEach(res -> publishEvent(publishProfileViewEvent, res));
        } else {
            publishEvent(publishProfileViewEvent, result);
        }
    }

    private void publishEvent(PublishProfileViewEvent event, Object result) {
        Arrays.stream(event.events())
                .forEach(eventClass -> {
                    EventPublisher publisher = eventPublisherMap.get(eventClass);
                    if (publisher != null) {
                        publisher.publishEvent(result);
                    }
                });
    }
}
