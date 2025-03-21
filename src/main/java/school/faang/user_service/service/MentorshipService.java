package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    public void deleteMentorship(Long mentorId, Long menteeId) {
        mentorshipRepository.findById(mentorId).ifPresent(mentor -> {
            mentor.getMentees().forEach(mentee -> {
                if (mentee.getId().equals(menteeId)) {
                    mentor.getMentees().remove(mentee);
                    mentee.getMentors().remove(mentor);

                    mentee.getGoals().forEach(goal -> {
                        Goal oneGoal = goalRepository.findById(goal.getId()).orElseThrow(() -> new RuntimeException("Goal not found"));
                        oneGoal.setMentor(mentee);

                        goalRepository.save(oneGoal);
                    });
                }
                userRepository.save(mentee);
            });
            mentorshipRepository.save(mentor);
        });
    }
}
