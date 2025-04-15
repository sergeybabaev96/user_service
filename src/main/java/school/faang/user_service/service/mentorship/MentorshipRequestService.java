package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRepository mentorshipRepository;

    public void requestMentorship(MentorshipRequestDto dto){

    }
}
