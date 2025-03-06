package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        User mentor = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Mentor doesn't exists"));
        return mentor.getMentees() != null ? mentor.getMentees().stream()
                .map(userMapper::toDto).toList() : List.of();

    }

    public List<UserDto> getMentors(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Mentee doesn't exists"));

        return user.getMentors() != null ? user.getMentors()
                .stream().map(userMapper::toDto).toList() : List.of();

    }
}