package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;
    private final MentorshipMapper mentorshipMapper;

    public List<MentorshipDto> getMentees(long userId) {
        return userRepository.findById(userId)
                .map(User::getMentees)
                .orElse(List.of())
                .stream()
                .map(mentorshipMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<MentorshipDto> getMentors(long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getMentors()
                        .stream()
                        .map(mentorshipMapper::toDto)
                        .toList())
                .orElseGet(List::of);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalStateException("Mentor not found."));
        if (!mentor.getMentees().removeIf(mentee -> mentee.getId() == menteeId)) {
            throw new IllegalStateException("The mentee was not found in the mentor's list.");
        }
        userRepository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new IllegalStateException("Mentee not found"));
        if (!mentee.getMentors().removeIf(mentor -> mentor.getId() == mentorId)) {
            throw new IllegalStateException("The mentor was not found in the mentee's list.");
        }
        userRepository.save(mentee);
    }
}
