package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private static final long TIME_FOR_REQUEST = 3;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRepository mentorshipRepository;


    @Transactional
    public void requestMentorship(MentorshipRequestDto dto) {
        long requesterId = dto.getRequester();
        long receiverId = dto.getReceiver();
        String description = dto.getDescription();

        if (!mentorshipRepository.existsById(requesterId) || !mentorshipRepository.existsById(receiverId)
        ) {
            throw new ResponseStatusException(NOT_FOUND, "User not found");
        }
        if (requesterId == receiverId) {
            throw new ResponseStatusException(BAD_REQUEST, "Requester and receiver are the same person.");
        }
        LocalDateTime today = LocalDateTime.now();
        Optional<LocalDateTime> dateLastRequest = mentorshipRequestRepository.findLatestRequestDate(requesterId, receiverId);
        if(dateLastRequest.isPresent()) {
            LocalDateTime lastRequest = dateLastRequest.get();
            if(lastRequest.isAfter(today.minusMonths(TIME_FOR_REQUEST))) {
                throw new ResponseStatusException(BAD_REQUEST, "You have already made a request during this period");
            }
        }
        mentorshipRequestRepository.create(requesterId, receiverId, description);
    }
}
