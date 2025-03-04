package school.faang.user_service.mentorshipTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.MentorshipService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @InjectMocks
    private MentorshipService mentorshipService;
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Captor
    ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("User1");
        List<User> mentees = new ArrayList<>();
        List<User> mentors = new ArrayList<>();
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("User2");
        User user3 = new User();
        user3.setId(3L);
        user3.setUsername("User3");
        mentees.add(user2);
        mentees.add(user3);
        User user4 = new User();
        user4.setId(4L);
        user4.setUsername("User4");
        User user5 = new User();
        user5.setId(5L);
        user5.setUsername("User5");
        mentors.add(user4);
        mentors.add(user5);
        user.setMentees(mentees);
        user.setMentors(mentors);
    }

    @Test
    void testGetMentees() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        MentorshipResponseDto user2 = new MentorshipResponseDto(2L, "User2");
        MentorshipResponseDto user3 = new MentorshipResponseDto(3L, "User3");
        List<MentorshipResponseDto> expected = Arrays.asList(user2, user3);

        List<MentorshipResponseDto> response = mentorshipService.getMentees(1L);
        Assertions.assertIterableEquals(expected, response);
    }

    @Test
    void testGetMentorsUserNotFound() {
        when(userRepository.findById(6L)).thenReturn(Optional.empty());
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.getMentors(6L));
        Assertions.assertEquals("User not found", e.getMessage());
    }

    @Test
    void testGetMentorsWhenNull() {
        user.setMentors(null);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        List<MentorshipResponseDto> response = mentorshipService.getMentors(1L);
        Assertions.assertEquals(0, response.size());
    }

    @Test
    void testDeleteMentee() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        List<User> result = new ArrayList<>();
        User menteeUser = new User();
        menteeUser.setId(3L);
        menteeUser.setUsername("User3");
        result.add(menteeUser);

        mentorshipService.deleteMentee(1L, 2L);
        verify(userRepository, times(1)).save(argumentCaptor.capture());
        User changedUser = argumentCaptor.getValue();

        Assertions.assertIterableEquals(result, changedUser.getMentees());
    }

    @Test
    void testDeleteMentor() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        List<User> result = new ArrayList<>();
        User mentorUser = new User();
        mentorUser.setId(5L);
        mentorUser.setUsername("User5");
        result.add(mentorUser);
        mentorshipService.deleteMentor(1L, 4L);
        verify(userRepository, times(1)).save(argumentCaptor.capture());
        User changedUser = argumentCaptor.getValue();

        Assertions.assertIterableEquals(result, changedUser.getMentors());
    }

    @Test
    void testDeleteMentee_UserNotFound() {
        when(userRepository.findById(6L)).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                mentorshipService.deleteMentee(6L, 2L));
    }

    @Test
    void testDeleteMentee_UserHasNoMentees() {
        user.setMentees(new ArrayList<>());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Exception exception = Assertions.assertThrows(IllegalStateException.class, () ->
                mentorshipService.deleteMentee(1L, 2L));
        Assertions.assertEquals("User has no mentees to remove", exception.getMessage());
    }

    @Test
    void testDeleteMentee_MenteeNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                mentorshipService.deleteMentee(1L, 99L));
        Assertions.assertEquals("Mentee not found in user's mentee list", exception.getMessage());
    }

    @Test
    void testDeleteMentor_UserNotFound() {
        when(userRepository.findById(6L)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.deleteMentor(6L, 4L));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testDeleteMentor_UserHasNoMentors() {
        user.setMentors(new ArrayList<>());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Exception exception = Assertions.assertThrows(IllegalStateException.class,
                () -> mentorshipService.deleteMentor(1L, 4L));

        Assertions.assertEquals("User has no mentors to remove", exception.getMessage());
    }

    @Test
    void testDeleteMentor_MentorNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> mentorshipService.deleteMentor(1L, 99L));

        Assertions.assertEquals("Mentor not found in user's mentors list", exception.getMessage());
    }
}