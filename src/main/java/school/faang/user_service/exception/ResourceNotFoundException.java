package school.faang.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException userNotFoundException(long userId) {
        return new ResourceNotFoundException("User with id %s not found".formatted(userId));
    }

    public static ResourceNotFoundException skillNotFoundException() {
        return new ResourceNotFoundException("Skill not found");
    }

    public static ResourceNotFoundException recommendationNotFoundException(long id) {
        return new ResourceNotFoundException("Recommendation with id %s not found".formatted(id));
    }

    public static ResourceNotFoundException userAvatarNotFoundException(long userId) {
        return new ResourceNotFoundException("UserAvatar with userId %s not found".formatted(userId));
    }

    public static ResourceNotFoundException premiumPeriodNotFoundException(long days) {
        return new ResourceNotFoundException("No PremiumPeriod found for days: %s".formatted(days));
    }
}
