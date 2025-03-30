package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @InjectMocks
    MentorshipService mentorshipService;

    @Mock
    MentorshipRepository mentorshipRepository;

    @Mock
    UserService userService;

    @Spy
    UserMapper userMapper;

    private final long userId = 1L;
    private final long menteeId = 2L;
    private final long mentorId = 3L;
    private final List<UserDto> menteeDtos = List.of(
            UserDto.builder().id(menteeId).username("username2").email("user2@test.com").build(),
            UserDto.builder().id(4L).username("username4").email("user4@test.com").build()
    );
    private final List<UserDto> mentorDtos = List.of(
            UserDto.builder().id(mentorId).username("username3").email("user3@test.com").build(),
            UserDto.builder().id(5L).username("username5").email("user5@test.com").build()
    );
    private final User user = User.builder()
            .id(userId)
            .username("username1")
            .email("user1@test.com")
            .mentees(new ArrayList<>(List.of(
                    User.builder().id(menteeId).username("username2").email("user2@test.com").build(),
                    User.builder().id(4L).username("username4").email("user4@test.com").build())))
            .mentors(new ArrayList<>(List.of(
                    User.builder().id(mentorId).username("username3").email("user3@test.com").build(),
                    User.builder().id(5L).username("username5").email("user5@test.com").build())))
            .build();


    @Test
    void getMentees_shouldThrowRuntimeExceptionWhenUserNotFindById() {
        when(userService.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> mentorshipService.getMentees(userId));
    }

    @Test
    void getMentees_shouldReturnMenteeDtosListWhenUserNotEmpty() {
        when(userService.findById(userId)).thenReturn(Optional.of(user));

        List<UserDto> result = mentorshipService.getMentees(userId);

        assertEquals(menteeDtos, result);
    }

    @Test
    void getMentors_shouldThrowRuntimeExceptionWhenUserNotFindById() {
        when(userService.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> mentorshipService.getMentors(userId));
    }

    @Test
    void getMentors_shouldReturnMentorsDtosListWhenUserNotEmpty() {
        when(userService.findById(userId)).thenReturn(Optional.of(user));

        List<UserDto> result = mentorshipService.getMentors(userId);

        assertEquals(mentorDtos, result);
    }

    @Test
    void deleteMentee_shouldThrowRuntimeExceptionWhenUserNotFindById() {
        when(mentorshipRepository.findById(menteeId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> mentorshipService.deleteMentee(menteeId, mentorId));
    }

    @Test
    void deleteMentee_shouldDeleteMenteeWhenUserNotEmpty() {
        User branchUser = user;
        when(mentorshipRepository.findById(userId)).thenReturn(Optional.of(branchUser));

        mentorshipService.deleteMentee(menteeId, userId);

        assertFalse(user.getMentees().stream().anyMatch(mentee -> mentee.getId() == menteeId),
                "Mentee should be deleted from the user's mentee list");
    }

    @Test
    void deleteMentor_shouldThrowRuntimeExceptionWhenUserNotFindById() {
        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> mentorshipService.deleteMentor(mentorId, menteeId));
    }

    @Test
    void deleteMentor_shouldDeleteMentorWhenUserNotEmpty() {
        User branchUser = user;
        when(mentorshipRepository.findById(userId)).thenReturn(Optional.of(branchUser));

        mentorshipService.deleteMentor(mentorId, userId);

        assertFalse(user.getMentors().stream().anyMatch(mentor -> mentor.getId() == mentorId),
                "Mentee should be deleted from the user's mentee list");
    }
}
