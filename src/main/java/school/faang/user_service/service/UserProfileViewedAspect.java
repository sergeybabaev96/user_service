package school.faang.user_service.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.kafka.KafkaProducer;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class UserProfileViewedAspect {
    private final UserContext userContext;
    private final KafkaProducer producer;

    @AfterReturning(pointcut = "@annotation(school.faang.user_service.utility.aspect_annotations.UserProfileViewed)",
            returning = "user")
    public void afterUserProfileViewed(User user) {
        Long viewerId = getContextUserId();
        Long profileOwnerId = user.getId();

        if (viewerId != 0 && notSameUser(viewerId, profileOwnerId) && hasContactPreference(user)) {
            log.info("User profile viewed: viewerId={}, profileOwnerId={}", viewerId, profileOwnerId);
            producer.sendMessage(viewerId, profileOwnerId);
        }
    }

    private Long getContextUserId() {
        try {
            return userContext.getUserId();
        } catch (IllegalArgumentException e) {
            log.debug(e.getMessage());
            return 0L;
        }
    }

    private boolean notSameUser(Long viewerId, Long profileOwnerId) {
        return !Objects.equals(viewerId, profileOwnerId);
    }

    private boolean hasContactPreference(User user) {
        return Objects.nonNull(user.getContactPreference())
                && Objects.nonNull(user.getContactPreference().getPreference());
    }
}
