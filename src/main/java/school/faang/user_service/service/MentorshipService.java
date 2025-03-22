package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;

    public void deleteMentorship(Long userId) {
        User mentor = userRepository.findById(userId)
                .orElseThrow(()->new EntityNotFoundException("Invalid user Id"));

        mentor.setMentees(null);
    }
}
