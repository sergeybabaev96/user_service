package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        validateId(userId);
        return getUserRelatedList(userId, User::getMentees);
    }

    public List<UserDto> getMentors(long userId) {
        validateId(userId);
        return getUserRelatedList(userId, User::getMentors);
    }

    @Transactional
    public void deleteMenteeAndMentor(long menteeId, long mentorId) {
        User mentor = getUserById(mentorId, "Mentor");
        User mentee = getUserById(menteeId, "Mentee");
        mentor.getMentees().remove(mentee);
        mentee.getMentors().remove(mentor);
        mentorshipRepository.save(mentor);
        mentorshipRepository.save(mentee);
    }

    private List<UserDto> getUserRelatedList(long userId, Function<User, List<User>> relationGetter) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User doesn't exists"));
        return Optional
                .ofNullable(relationGetter.apply(user))
                .orElse(List.of()).stream()
                .map(userMapper::toDto)
                .toList();
    }

    private User getUserById(long userId, String user){
        return mentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(user + " with id: " + userId + " is not in the database"));
    }

    private void validateId(long id){
        if (id < 1) {
            throw new ConstraintViolationException("mentorId must be greater than 0", null);
        }
    }
}