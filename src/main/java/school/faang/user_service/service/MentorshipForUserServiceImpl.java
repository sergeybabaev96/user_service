package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.MentorshipRepository;

@Service
@RequiredArgsConstructor
public class MentorshipForUserServiceImpl implements MentorshipForUserService {

    private final MentorshipRepository mentorshipRepository;

    @Override
    public void deleteFromMentorShipDeactivatedUser(Long userId) {
        mentorshipRepository.deleteDeactivateUser(userId);
    }
}
