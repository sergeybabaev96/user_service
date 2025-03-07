package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        User mentor = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Mentor doesn't exists"));
        return mentor.getMentees() != null ? mentor.getMentees().stream()
                .map(userMapper::toDto).toList() : List.of();

    }

    public List<UserDto> getMentors(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Mentee doesn't exists"));

        return user.getMentors() != null ? user.getMentors()
                .stream().map(userMapper::toDto).toList() : List.of();

    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor doesn't exists"));

        boolean removedMentee = mentor.getMentees().removeIf(mentee -> mentee.getId() == menteeId);

        if (!removedMentee) {
            log.error("Mentee with id {} not found for mentor with id {} ", menteeId, mentorId);
            throw new RuntimeException("Mentee not found  for given mentor");
        }

        userRepository.save(mentor);
    }

    public void deleteMentor(long mentorId, long menteeId) {
        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new RuntimeException("Mentee doesn't exists"));

        boolean removedMentor = mentee.getMentors().removeIf(mentor -> mentor.getId() == mentorId);

        if (!removedMentor) {
            log.error("Mentor with id {} not found for mentee with id {} ", mentorId, menteeId);
        }
    }
}