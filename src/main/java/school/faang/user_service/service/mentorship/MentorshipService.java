package school.faang.user_service.service.mentorship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;

    public List<User> getMentees(Long userId) {
        return mentorshipRepository.findMenteesByMentorId(userId);
    }

    public List<User> getMentors(Long userId) {
        return mentorshipRepository.findMentorsByMenteeId(userId);
    }

    public void deleteMentee(Long mentorId, Long menteeId) {
        boolean exists = mentorshipRepository.existsByMentorIdAndMenteeId(mentorId, menteeId);
        if (exists) {
            mentorshipRepository.deleteByMentorIdAndMenteeId(mentorId, menteeId);
        } else {
            throw new IllegalArgumentException("Mentor does not have this mentee.");
        }
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        boolean exists = mentorshipRepository.existsByMentorIdAndMenteeId(mentorId, menteeId);
        if (exists) {
            mentorshipRepository.deleteByMenteeIdAndMentorId(menteeId, mentorId);
        } else {
            throw new IllegalArgumentException("Mentee does not have this mentor.");
        }
    }
}
