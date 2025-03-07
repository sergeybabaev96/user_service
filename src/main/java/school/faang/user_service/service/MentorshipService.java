package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipMapper mentorshipMapper;
    private final UserRepository userRepository;

    public List<MentorshipDto> getMentees(long mentorId) {
        List<User> mentees = mentorshipRepository.findAllMenteesByMentorId(mentorId);
        return mentees.stream()
                .map(mentorshipMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<MentorshipDto> getMentors(long userId) {
        List<User> mentors = userRepository.findAllById(Collections.singleton(userId));
        return mentors.stream()
                .map(mentorshipMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteMentee(long menteeId, long mentorId) {
        List<User> mentee = mentorshipRepository.findAllMenteesByMentorId(mentorId);
        boolean isMentee = mentee.stream()
                .anyMatch(user -> user.getId().equals(menteeId));

        if (isMentee) {
            mentorshipRepository.deleteById(menteeId);
        } else {
            throw new IllegalArgumentException("Ментор не ментерит данного менти");
        }
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        List<MentorshipDto> mentors = getMentors(menteeId);

        boolean isMentor = mentors.stream()
                .anyMatch(mentor -> mentor.getId().equals(mentorId));
        if (isMentor) {
            mentorshipRepository.deleteById(mentorId);
        } else {
            throw new IllegalArgumentException("Ментор не ментерит данного менти");
        }
    }
}
