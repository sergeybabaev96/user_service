package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        log.info("Fetching mentees for user with ID {}", userId);
        UserDto userDto = getUserDto(userId);

        if (userDto.getMenteesIds().isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> menteesIds = userDto.getMenteesIds();
        List<UserDto> usersDto = new ArrayList<>();
        for (Long menteeId : menteesIds) {
            UserDto menteeDto = getUserDto(menteeId);
            usersDto.add(menteeDto);
        }
        return usersDto;
    }

    public List<UserDto> getMentors(long userId) {
        log.info("Fetching mentors for user with ID {}", userId);
        UserDto userDto = getUserDto(userId);

        if (userDto.getMentorsIds().isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> mentorsIds = userDto.getMentorsIds();
        List<UserDto> usersDto = new ArrayList<>();
        for (Long mentorId : mentorsIds) {
            UserDto mentorDto = getUserDto(mentorId);
            usersDto.add(mentorDto);
        }
        return usersDto;
    }

    public void deleteMentee(long menteeId, long mentorId) {
        log.info("Attempting to delete mentee {} from mentor {}", menteeId, mentorId);

        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));
        List<User> mentees = mentor.getMentees();

        boolean isRemoved = mentees.removeIf(user -> user.getId() == menteeId);

        if (!isRemoved) {
            log.error("Failed to find mentee {} under mentor {}", menteeId, mentorId);
            throw new IllegalArgumentException("Mentee not found");
        }
        mentor.setMentees(mentees);
        mentorshipRepository.save(mentor);
        log.info("Successfully deleted mentee {} from mentor {}", menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        log.info("Attempting to delete mentor {} from mentee {}", mentorId, menteeId);

        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new IllegalArgumentException("Mentee not found"));
        List<User> mentors = mentee.getMentors();

        boolean isRemoved = mentors.removeIf(user -> user.getId() == mentorId);
        if (!isRemoved) {
            log.error("Failed to find mentor {} under mentee {}", mentorId, menteeId);
            throw new IllegalArgumentException("Mentor not found");
        }
        mentee.setMentors(mentors);
        mentorshipRepository.save(mentee);
        log.info("Successfully deleted mentor {} from mentee {}", mentorId, menteeId);
    }

    private UserDto getUserDto(long userId) {
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Long> mentees = user.getMentees().stream()
                .map(User::getId)
                .toList();
        List<Long> mentors = user.getMentors().stream()
                .map(User::getId)
                .toList();
        UserDto userDto = userMapper.toDto(user);
        userDto.setMenteesIds(mentees);
        userDto.setMentorsIds(mentors);
        return userDto;
    }
}
