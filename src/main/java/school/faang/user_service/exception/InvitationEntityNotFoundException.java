package school.faang.user_service.exception;

import java.util.NoSuchElementException;

public class InvitationEntityNotFoundException extends NoSuchElementException {
    public InvitationEntityNotFoundException(String message) {
        super(message);
    }
}
