package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.MentorshipNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(Long mentorId) {
        List<User> mentees = mentorshipRepository.findAllMenteesByMentorId(mentorId);
        return mentees.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserDto> getMentors(Long menteeId) {
        List<User> mentors = mentorshipRepository.findAllMentorsByMenteeId(menteeId);
        return mentors.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteMentee(Long menteeId, Long mentorId) {
        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new MentorshipNotFoundException("Mentor not found: " + mentorId));
        boolean removed = mentor.getMentees().removeIf(mentee -> mentee.getId().equals(menteeId));
        if (!removed) {
            throw new MentorshipNotFoundException("No mentorship relationship found for mentor "
                    + mentorId + " and mentee " + menteeId);
        }
        mentorshipRepository.save(mentor);
    }

    @Transactional
    public void deleteMentor(Long menteeId, Long mentorId) {
        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new MentorshipNotFoundException("Mentee not found: " + menteeId));
        boolean removed = mentee.getMentors().removeIf(mentor -> mentor.getId().equals(mentorId));
        if (!removed) {
            throw new MentorshipNotFoundException("No mentorship relationship found for mentee "
                    + menteeId + " and mentor " + mentorId);
        }
        mentorshipRepository.save(mentee);
    }

    public void deleteMentorShipByDeactivatedUser(Long mentorID) {
        mentorshipRepository.deactivateMentor(mentorID);
    }

    public void deleteMenteeByDeactivatedUser(Long menteeId) {
        mentorshipRepository.deleteDeactivatedMentee(menteeId);
    }
}
