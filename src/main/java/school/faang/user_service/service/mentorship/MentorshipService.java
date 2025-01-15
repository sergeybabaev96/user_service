package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private static final String MENTOR_ID_TYPE = "Mentor ID";
    private static final String MENTEE_ID_TYPE = "Mentee ID";

    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;


    public List<UserDto> getMentees(long mentorId) {
        validateId(mentorId, MENTOR_ID_TYPE);
        return userRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Mentor with id %d not found", mentorId)))
                .getMentees().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserDto> getMentors(long menteeId) {
        validateId(menteeId, MENTEE_ID_TYPE);
        return userRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Mentee with id %d not found", menteeId)))
                .getMentors().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public void deleteMentorship(long mentorId, long menteeId) {
        validateId(mentorId, MENTOR_ID_TYPE);
        validateId(menteeId, MENTEE_ID_TYPE);
        if (!mentorshipRepository.existsByMentorIdAndMenteeId(mentorId, menteeId)) {
            throw new EntityNotFoundException(
                    String.format("Mentorship between mentor %d and mentee %d not found", mentorId, menteeId)
            );
        }
        mentorshipRepository.deleteMentorship(mentorId, menteeId);
    }

    private void validateId(long id, String idType) {
        if (id <= 0) {
            throw new IllegalArgumentException(String.format("%s must be positive", idType));
        }
    }

}
