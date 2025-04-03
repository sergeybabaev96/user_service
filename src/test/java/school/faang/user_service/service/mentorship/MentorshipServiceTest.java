package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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

    private User user;

    @BeforeEach
    public void setUser() {
        user = User.builder()
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
    }

    @Test
    void getMentees_shouldThrowExceptionWhenUserNotFindById() {
        when(userService.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentees(userId));
    }

    @Test
    void getMentees_shouldReturnMenteeDtos() {
        when(userService.findById(userId)).thenReturn(Optional.of(user));

        List<UserDto> result = mentorshipService.getMentees(userId);

        assertEquals(menteeDtos, result);
    }

    @Test
    void getMentors_shouldThrowExceptionWhenUserNotFindById() {
        when(userService.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentors(userId));
    }

    @Test
    void getMentors_shouldReturnMentorsDtos() {
        when(userService.findById(userId)).thenReturn(Optional.of(user));

        List<UserDto> result = mentorshipService.getMentors(userId);

        assertEquals(mentorDtos, result);
    }

    @Test
    void deleteMentee_shouldThrowWhenUserNotFindById() {
        when(mentorshipRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.deleteMentee(menteeId, userId));
    }

    @Test
    void deleteMentee_shouldDeleteMentee() {
        when(mentorshipRepository.findById(userId)).thenReturn(Optional.of(user));

        mentorshipService.deleteMentee(menteeId, userId);

        assertFalse(user.getMentees().stream().anyMatch(mentee -> mentee.getId() == menteeId),
                "Mentee should be deleted from the user's mentee list");
    }

    @Test
    void deleteMentor_shouldThrowExceptionWhenUserNotFindById() {
        when(mentorshipRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.deleteMentor(mentorId, userId));
    }

    @Test
    void deleteMentor_shouldDeleteMentor() {
        when(mentorshipRepository.findById(userId)).thenReturn(Optional.of(user));

        mentorshipService.deleteMentor(mentorId, userId);

        assertFalse(user.getMentors().stream().anyMatch(mentor -> mentor.getId() == mentorId),
                "Mentee should be deleted from the user's mentee list");
    }
}
