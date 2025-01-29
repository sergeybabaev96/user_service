package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

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

    private void removeMentorFromGoals(User mentee,Long userId) {
        mentee.setGoals(mentee.getGoals().stream()
                .filter(goal -> Objects.equals(goal.getMentor().getId(), userId))
                .peek(goal -> goal.setMentor(mentee))
                .toList());
        userRepository.save(mentee);
    }

    public List<Long> getMentees(Long userId) {

        User user = mentorshipRepository.findById(userId).orElse(null);

        return user != null ? StreamSupport.stream(mentorshipRepository.
                        findAllById(user.getMentees().stream().map(User::getId).toList()).spliterator(), false).
                map(User::getId).toList() : Collections.emptyList();
    }

    public List<Long> getMentors(Long userId) {

        User user = mentorshipRepository.findById(userId).orElse(null);

        return user != null ? StreamSupport.stream(mentorshipRepository.
                        findAllById(user.getMentors().stream().map(User::getId).toList()).spliterator(), false).
                map(User::getId).toList() : Collections.emptyList();
    }

    public void deleteMentee(Long mentorId, Long menteeId) {
        User user = mentorshipRepository.findById(mentorId).
                orElseThrow(() -> new NoSuchElementException("Mentor not found"));

        mentorshipRepository.delete(user.getMentees().stream().
                filter(m -> Objects.equals(m.getId(), menteeId)).findAny().
                orElseThrow(() -> new NoSuchElementException("Mentee not found")));
    }

    public void deleteMentor(Long mentorId, Long menteeId) {
        User user = mentorshipRepository.findById(menteeId).
                orElseThrow(() -> new NoSuchElementException("Mentee not found"));

        mentorshipRepository.delete(user.getMentors().stream().
                filter(m -> Objects.equals(m.getId(), mentorId)).findAny().
                orElseThrow(() -> new NoSuchElementException("Mentor not found")));
    }

}
