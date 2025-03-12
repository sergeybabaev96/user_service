package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.mentorship.MentorshipDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MentorshipService {

    private static final String MENTOR_NOT_FOUND_MESSAGE = "Mentor with id %d not found";
    private static final String MENTEE_NOT_FOUND_MESSAGE = "Mentee with id %d not found";
    private static final String MENTORSHIP_NOT_FOUND_MESSAGE = "Mentorship between mentor %d and mentee %d not found";

    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipMapper mentorshipMapper;

    public List<MentorshipDto> getMentees(@NotNull @Positive Long mentorId) {
        return userRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(MENTOR_NOT_FOUND_MESSAGE, mentorId)))
                .getMentees()
                .stream()
                .map(mentorshipMapper::toDto)
                .toList();
    }

    public List<MentorshipDto> getMentors(@NotNull @Positive Long menteeId) {
        return userRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(MENTEE_NOT_FOUND_MESSAGE, menteeId)))
                .getMentors()
                .stream()
                .map(mentorshipMapper::toDto)
                .toList();
    }

    public void deleteByMentorIdAndMenteeId(@NotNull @Positive Long mentorId,
                                            @NotNull @Positive Long menteeId) {
        if (mentorshipRepository.existsByMentorIdAndMenteeId(mentorId, menteeId)) {
            mentorshipRepository.deleteByMentorIdAndMenteeId(mentorId, menteeId);
        } else {
            throw new EntityNotFoundException(
                    String.format(MENTORSHIP_NOT_FOUND_MESSAGE, mentorId, menteeId)
            );
        }
    }

    /**
     * - deletes mentees from mentor
     * - changes mentee_id of goals created by mentor
     * to id of first user associated with the goal.
     * @param user - mentor who is being deactivated
     */
    public void stopMentorship(User user) {
        List<Goal> goalsControlledByUser = user.getSetGoals();
        for(Goal goal : goalsControlledByUser) {
            List<User> usersOfGoal = goal.getUsers();
            List<User> usersOfGoalWithoutDeactivatedUser = usersOfGoal.stream()
                    .filter(u -> !u.getId().equals(user.getId()))
                    .collect(Collectors.toList());
            goal.setMentor(usersOfGoalWithoutDeactivatedUser.get(0));
            log.debug("Goal \"{}\" has changed mentor_id: old Id={} , new Id=\"{}\"",
                    goal.getDescription(),
                    user.getId(),
                    usersOfGoalWithoutDeactivatedUser.get(0).getId());
        }
        user.getSetGoals().removeAll(goalsControlledByUser);

        mentorshipRepository.deleteByMentorId(user.getId());
        log.debug("Deleting mentees for mentor with mentor_id={}", user);
    }
}
