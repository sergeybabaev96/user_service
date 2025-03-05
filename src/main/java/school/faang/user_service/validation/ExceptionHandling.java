package school.faang.user_service.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExceptionHandling {
    public void handleException(Exception e) {
        log.error("Message: {}\nException: {}, stack trace: {}", e.getMessage(), e, e.getStackTrace());
    }
}
