package school.faang.user_service.exception;

import java.util.NoSuchElementException;

public class UserNotFoundException extends NoSuchElementException {
    public UserNotFoundException(long id) {
        super(String.format("User with id %d not found", id));
    }
}
