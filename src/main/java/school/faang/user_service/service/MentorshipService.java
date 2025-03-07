package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipMapper mentorshipMapper;

    public List<MentorshipDto> getMentees(long userId) {
        List<User> mentees = mentorshipRepository.findAllMenteesByUserId(userId);
        return mentorshipMapper.toDtos(mentees);
    }

    public List<MentorshipDto> getMentors(long userId) {
        List<User> mentors = mentorshipRepository.findAllMentorsByUserId(userId);
        return mentorshipMapper.toDtos(mentors);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User user = mentorshipRepository.findUserById(mentorId).orElseThrow(() -> new IllegalStateException("Mentor not found."));
        List<User> mentees = mentorshipRepository.findAllMenteesByUserId(user.getId());
        if (mentees.isEmpty()) {
            throw new IllegalStateException("Mentor has not mentees");
        }
        boolean removed = user.getMentees().removeIf(mentee -> mentee.getId() == menteeId);
        if (!removed) {
            throw new IllegalStateException("The mentee was not found in the mentor's list.");
        }
        mentorshipRepository.save(user);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User user = mentorshipRepository.findUserById(menteeId).orElseThrow(() -> new IllegalStateException("Mentee not found"));
        List<User> mentors = mentorshipRepository.findAllMentorsByUserId(user.getId());
        if (mentors.isEmpty()) {
            throw new IllegalStateException("Mentee has not mentors");
        }
        boolean removed = user.getMentors().removeIf(mentor -> mentor.getId() == mentorId);
        if (!removed) {
            throw new IllegalStateException("The mentor was not found in the mentee's list.");
        }
        mentorshipRepository.save(user);
    }
}
