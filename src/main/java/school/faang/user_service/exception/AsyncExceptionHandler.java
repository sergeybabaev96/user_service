package school.faang.user_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(@NotNull Throwable ex, Method method, Object... params) {
        log.error(String.format("Async Exception in method %s: %s",
                method.getName(), ex.getMessage()), ex);
    }
}
