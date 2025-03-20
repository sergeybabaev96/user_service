package school.faang.user_service.exception;

import jakarta.persistence.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException  {

    public UserNotFoundException(Long id) {
        super(String.format("User with id = %d doesn't exist", id));
    }

}
