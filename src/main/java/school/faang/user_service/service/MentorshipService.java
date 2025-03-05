package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        User mentor = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Mentor doesn't exists"));
        return mentor.getMentees().stream().map(userMapper::toDto).toList();

    }
}