package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        return getUserRelatedList(userId, User::getMentees);
    }

    public List<UserDto> getMentors(long userId) {
        return getUserRelatedList(userId, User::getMentors);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        deleteUserFromRelation(mentorId, menteeId, true);
    }

    public void deleteMentor(long mentorId, long menteeId) {
        deleteUserFromRelation(menteeId, mentorId, false);
    }


    private List<UserDto> getUserRelatedList(long userId, Function<User, List<User>> relationGetter) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User doesn't exists"));
        return relationGetter
                .apply(user) != null ? relationGetter.apply(user)
                .stream().map(userMapper::toDto)
                .toList() : List.of();
    }

    public void deleteUserFromRelation(long ownerId, long targetId, boolean isMentee) {
        User owner = mentorshipRepository.findById(ownerId).orElseThrow(() ->
                new RuntimeException(isMentee ? "Mentor doesn't exist" : "Mentee doesn't exist"));

        boolean removed = (isMentee ? owner.getMentees() : owner.getMentors())
                .removeIf(user -> user.getId() == targetId);

        if (!removed) {
            log.error("{} with id {} not found for {} with id {}",
                    isMentee ? "Mentee" : "Mentor", targetId,
                    isMentee ? "mentor" : "mentee", ownerId);
            throw new RuntimeException(isMentee ? "Mentee not found for given mentor" : "Mentor not found for given mentee");
        }
        userRepository.save(owner);

    }
}