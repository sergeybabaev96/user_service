package school.faang.user_service.validators;

public interface Validator<T> {
    void validate(T t);
}
