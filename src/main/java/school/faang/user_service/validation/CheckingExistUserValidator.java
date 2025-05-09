package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
public class CheckingExistUserValidator implements MentorshipValidator {

    public final MentorshipRepository mentorshipRepository;

    @Override
    public void validate(MentorshipRequestDto dto) {
        if (!mentorshipRepository.existsById(dto.getRequester()) || !mentorshipRepository.existsById(dto.getReceiver())
        ) {
            throw new ResponseStatusException(NOT_FOUND, "RequesterId or receiverId not found");
        }
    }
}