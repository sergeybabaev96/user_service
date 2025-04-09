package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipMapper mentorshipMapper;
    private final UserRepository userRepository;

    public void deleteMentorship(Long userId) {
        User mentor = userRepository.findById(userId)
                .orElseThrow(()-> {
                    log.error("User with id {} not found", userId);
                    return new EntityNotFoundException("Invalid user Id");
                });
        mentor.setMentees(null);
    }

    public List<MentorshipDto> getMentees(long mentorId) {
        List<User> mentees = mentorshipRepository.findAllMenteesByMentorId(mentorId);
        if (mentees == null || mentees.isEmpty()) {
            return Collections.emptyList();
        }
        return mentees.stream()
                .map(mentorshipMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<MentorshipDto> getMentors(long userId) {
        List<User> mentors = userRepository.findAllById(Collections.singleton(userId));
        if (mentors == null || mentors.isEmpty()) {
            return Collections.emptyList();
        }
        return mentors.stream()
                .map(mentorshipMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteMentee(long menteeId, long mentorId) {
        List<User> mentee = mentorshipRepository.findAllMenteesByMentorId(mentorId);
        if (mentee == null || mentee.isEmpty()) {
            log.error("mentorshipRepository.findAllMenteesByMentorId вернул null для mentorId={}", mentorId);
            throw new IllegalArgumentException("У ментора с ID %d нет ни одного менти".formatted(mentorId));
        }
        boolean isMentee = mentee.stream()
                .filter(Objects::nonNull)
                .anyMatch(user -> user.getId().equals(menteeId));

        if (isMentee) {
            mentorshipRepository.deleteById(menteeId);
            log.info("Удалён менти с ID {} у ментора с ID {}", menteeId, mentorId);
        } else {
            log.warn("Ментор с ID {} не менторит менти с ID {}, удаление невозможно", mentorId, menteeId);
            throw new IllegalArgumentException("Ментор с ID %d не ментерит менти с ID %d".formatted(mentorId, menteeId));
        }
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        List<MentorshipDto> mentors = getMentors(menteeId);
        if (mentors == null || mentors.isEmpty()) {
            log.error("getMentors вернул null для menteeId={}", menteeId);
            throw new IllegalArgumentException("У менти с ID %d нет менторов".formatted(menteeId));
        }
        boolean isMentor = mentors.stream()
                .filter(Objects::nonNull)
                .anyMatch(mentor -> mentor.getId().equals(mentorId));

        if (isMentor) {
            mentorshipRepository.deleteById(mentorId);
            log.info("Удалён ментор с ID {} у менти с ID {}", mentorId, menteeId);
        } else {
            log.warn("Ментор с ID {} не является наставником менти с ID {}, удаление невозможно", mentorId, menteeId);
            throw new IllegalArgumentException("Ментор с ID %d не ментерит менти с ID %d".formatted(mentorId, menteeId));
        }
    }
}
