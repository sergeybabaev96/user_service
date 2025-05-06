package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;


    public void requestMentorship(MentorshipRequestDto dto) {
       long requesterId = dto.getRequester();
       long receiverId = dto.getReceiver();
       String description = dto.getDescription();
        mentorshipRequestRepository.create(requesterId, receiverId, description);
    }
}
