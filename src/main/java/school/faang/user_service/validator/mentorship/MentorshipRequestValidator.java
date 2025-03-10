package school.faang.user_service.validator.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {
    private final int REQUEST_FREQUENCY = 3;

    private final MentorshipRequestRepository requestRepository;

    public boolean validateLastRequestData(long requesterId, long receiverId) {
        Optional<MentorshipRequest> request = requestRepository.findLatestRequest(requesterId, receiverId);
        if (request.isEmpty()) {
            return true;
        } else {
            LocalDateTime earliestTimeRequests = LocalDateTime.now().minusMonths(REQUEST_FREQUENCY);
            return (request.get().getCreatedAt().isBefore(earliestTimeRequests));
        }
    }

    public boolean validateNotMentorYet(User requester, User receiver) {
        if (requester.getMentors().contains(receiver)) {
            throw new IllegalArgumentException("Requested mentor is already mentoring this user");
        } else {
            return true;
        }
    }
}
