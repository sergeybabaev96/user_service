package school.faang.user_service.validation;

import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.MentorshipRequestDto;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class RequesterIdNotBeEqualReceiverIdValidator implements MentorshipValidator{

    @Override
    public void validate(MentorshipRequestDto dto) {
        if (dto.getRequester() == dto.getReceiver()) {
            throw new ResponseStatusException(BAD_REQUEST, "Requester and receiver are the same person.");
        }
    }
}
