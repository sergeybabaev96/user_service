package school.faang.user_service.dtovalidator;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Predicate;

@Slf4j
public abstract class BaseParamsValidator<T> {
    private final StringBuilder errors = new StringBuilder();

    public abstract void validate(T t);

    protected <U> void addParam(U validatingObject, Predicate<U> validatorHandler, String errorMessage) {
        if (validatorHandler.test(validatingObject)) {
            log.trace("add param for validate: {}", validatingObject);
            if (!errors.isEmpty()) {
                errors.append("; ");
            }
            errors.append(errorMessage);
        }
    }

    protected void check(boolean throwException) {
        if (!errors.isEmpty() && throwException) {
            throw new IllegalArgumentException(errors.toString());
        }
    }
}
