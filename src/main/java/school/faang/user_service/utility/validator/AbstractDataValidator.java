package school.faang.user_service.utility.validator;

import school.faang.user_service.exception.DataValidationException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public abstract class AbstractDataValidator<T> {

    public abstract void validate(T data);

    public void checkNotNull(Object value, String errorMessage) {
        if (value == null) {
            throw new DataValidationException(errorMessage);
        }
    }

    public void checkStringNotNullOrEmpty(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new DataValidationException(errorMessage);
        }
    }

    public void checkNumberIsPositive(Number number, String errorMessage) {
        double numericValue = number.doubleValue();
        if (numericValue <= 0) {
            throw new DataValidationException(errorMessage);
        }
    }

    public void checkCollectionNotNullOrEmpty(Collection<?> collection, String errorMessage) {
        if (collection == null || collection.isEmpty()) {
            throw new DataValidationException(errorMessage);
        }
    }

    public void checkEnumValue(Enum<?> value, Enum<?>[] validValues, String errorMessage) {
        if (Arrays.stream(validValues).noneMatch(validValue -> Objects.equals(validValue, value))) {
            throw new DataValidationException(errorMessage);
        }
    }
}
