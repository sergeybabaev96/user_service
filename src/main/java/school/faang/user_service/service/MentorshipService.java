package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    @Transactional
    public void stopMentorship(User mentor) {
        mentor.getMentees().forEach(mentee -> {
            mentee.getMentors().remove(mentor);
            mentee.getGoals().forEach(goal -> {
                if (goal.getMentor().equals(mentor)) {
                    goal.setMentor(null);
                    goalRepository.save(goal);
                }
            });
        });

        mentor.getMentees().clear();

        userRepository.save(mentor);
    }
}
