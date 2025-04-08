package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
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
        return getUserRelatedList(userId, User::getMentees);
    }

    public List<UserDto> getMentors(long userId) {
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

    public void deleteMentee(long menteeId, long mentorId) {
        deleteUserFromRelation(mentorId, menteeId, true);
    }

    public void deleteMentor(long mentorId, long menteeId) {
        deleteUserFromRelation(menteeId, mentorId, false);
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

    private void deleteUserFromRelation(long ownerId, long targetId, boolean isMentee) {
        User owner = mentorshipRepository.findById(ownerId).orElseThrow(() ->
                new EntityNotFoundException(isMentee ? "Mentor doesn't exist" : "Mentee doesn't exist"));

        boolean removed = (isMentee ? owner.getMentees() : owner.getMentors())
                .removeIf(user -> user.getId() == targetId);

        if (!removed) {
            log.error("{} with id {} not found for {} with id {}",
                    isMentee ? "Mentee" : "Mentor", targetId,
                    isMentee ? "mentor" : "mentee", ownerId);
            throw new EntityNotFoundException(isMentee ? "Mentee not found for given mentor" : "Mentor not found for given mentee");
        }
        userService.save(owner);
        }
    private User getUserById(long userId, String user){
        return mentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(user + " with id: " + userId + " is not in the database"));
    }

}