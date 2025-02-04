package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;

    @Override
    public List<UserDto> getMentees(long userId) {
        return userRepository.findById(userId)
                .map(user -> userMapper.toDto(mentorshipRepository.findMenteesById(userId)))
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id = %d not found", userId)));
    }

    @Override
    public List<UserDto> getMentors(long userId) {
        return userRepository.findById(userId)
                .map(user -> userMapper.toDto(mentorshipRepository.findMentorsById(userId)))
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id = %d not found", userId)));
    }

    @Override
    public void deleteMentee(long menteeId, long mentorId) {
        Optional<User> mentor = userRepository.findById(mentorId);

        if (mentor.isPresent()) {
            mentor.get().setMentees(
                    mentorshipRepository.findMenteesById(mentorId).stream()
                            .filter(mentee -> mentee.getId() != menteeId)
                            .toList()
            );
        }
    }

    @Override
    public void deleteMentor(long menteeId, long mentorId) {
        Optional<User> mentee = userRepository.findById(menteeId);

        mentee.ifPresent(user -> user.setMentors(
                mentorshipRepository.findMentorsById(mentorId).stream()
                        .filter(mentor -> mentor.getId() != mentorId)
                        .toList()
        ));
    }

}
