package school.faang.user_service.validators;

public interface DtoValidator<T> {
    void validate(T t);
}
