package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;

    public void stopUserMentorship(Long userId) {
        mentorshipRepository.findById(userId).ifPresent(user -> {
            if (user.getMentees() != null) {
                user.getMentees().forEach(mentee -> {
                    removeMentorFromMentees(mentee);
                    removeMentorFromGoals(mentee, userId);
                });
            }
        });
    }

    private void removeMentorFromMentees(User mentee) {
        mentee.setMentors(mentee.getMentors().stream()
                .filter(mentor -> !Objects.equals(mentor.getId(), mentee.getId()))
                .toList());
        userRepository.save(mentee);
    }

    private void removeMentorFromGoals(User mentee, Long userId) {
        mentee.setGoals(mentee.getGoals().stream()
                .filter(goal -> Objects.equals(goal.getMentor().getId(), userId))
                .peek(goal -> goal.setMentor(mentee))
                .toList());
        userRepository.save(mentee);
    }

    @Transactional(readOnly = true)
    public List<Long> getMentees(Long userId) {
        return getIdsFromUser(userId, User::getMentees);
    }

    @Transactional(readOnly = true)
    public List<Long> getMentors(Long userId) {
        return getIdsFromUser(userId, User::getMentors);
    }

    @Transactional
    public void deleteMentee(Long mentorId, Long menteeId) {
        deleteUserFromList(mentorId, menteeId, User::getMentees, "Mentee not found");
    }

    @Transactional
    public void deleteMentor(Long mentorId, Long menteeId) {
        deleteUserFromList(menteeId, mentorId, User::getMentors, "Mentor not found");
    }

    private List<Long> getIdsFromUser(Long userId, UserListExtractor extractor) {
        return mentorshipRepository.findById(userId)
                .map(user -> extractor.extract(user).stream()
                        .map(User::getId)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    private void deleteUserFromList(Long userId,
                                    Long targetId,
                                    Function<User, List<User>> listExtractor,
                                    String errorMessage) {
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        User userToDelete = listExtractor.apply(user).stream()
                .filter(u -> Objects.equals(u.getId(), targetId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(errorMessage));

        mentorshipRepository.delete(userToDelete);
    }

    @FunctionalInterface
    private interface UserListExtractor {
        List<User> extract(User user);
    }
}
