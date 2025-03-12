package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserViewDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;

    @Spy
    private UserMapper userMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    private long menteeId;
    private long mentorId;
    private long userId;

    private User user;
    private User mentor;
    private User mentee;
    private List<User> mentees;
    private List<User> mentors;

    @BeforeEach
    public void init() {
        userId = 1L;
        menteeId = 2L;
        mentorId = 3L;

        user = new User();
        user.setId(userId);

        mentor = new User();
        mentor.setId(mentorId);
        mentor.setMentors(new ArrayList<>());

        mentee = new User();
        mentee.setId(menteeId);
        mentee.setMentees(new ArrayList<>());

        mentees = new ArrayList<>();
        mentees.add(mentee);
        mentor.setMentees(mentees);

        mentors = new ArrayList<>();
        mentors.add(mentor);
        mentee.setMentors(mentors);

        user.setMentees(mentees);
        user.setMentors(mentors);
    }

    @Test
    public void testGetMenteesPositive() {
        Mockito.when(mentorshipRepository.findById(userId))
                .thenReturn(Optional.of(user));

        UserViewDto menteeDto = new UserViewDto();

        doReturn(menteeDto)
                .when(userMapper)
                .toViewDto(mentee);

        List<UserViewDto> result = mentorshipService.getMentees(user.getId());
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void testGetMenteesIsEmpty() {
        user.setMentees(new ArrayList<>());

        Mockito.when(mentorshipRepository.findById(userId))
                .thenReturn(Optional.of(user));

        List<UserViewDto> result = mentorshipService.getMentees(user.getId());
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void testGetMentorsPositive() {
        Mockito.when(mentorshipRepository.findById(userId))
                .thenReturn(Optional.of(user));

        UserViewDto mentorDto = new UserViewDto();

        doReturn(mentorDto)
                .when(userMapper)
                .toViewDto(mentor);

        List<UserViewDto> result = mentorshipService.getMentors(user.getId());
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void testGetMentorsIsEmpty() {
        user.setMentors(new ArrayList<>());

        Mockito.when(mentorshipRepository.findById(userId))
                .thenReturn(Optional.of(user));

        List<UserViewDto> result = mentorshipService.getMentors(user.getId());
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void testDeleteMenteeSuccess() {
        Mockito.when(mentorshipRepository.findById(mentorId))
                .thenReturn(Optional.of(mentor));

        mentorshipService.deleteMentee(menteeId, mentorId);

        Mockito.verify(mentorshipRepository).save(Mockito.any());
    }

    @Test
    public void testDeleteMenteeInvalidMentee() {
        doReturn(Optional.of(mentor))
                .when(mentorshipRepository)
                .findById(mentorId);

        Exception exception = Assert.assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentee(userId, mentorId));
        Assertions.assertEquals("Менти не найден", exception.getMessage());
    }

    @Test
    public void testDeleteMenteeInvalidUser() {
        doReturn(Optional.empty())
                .when(mentorshipRepository)
                .findById(mentorId);
        Exception exception = Assert.assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentee(menteeId, mentorId));
        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void testDeleteMentorSuccess() {
        doReturn(Optional.of(mentee))
                .when(mentorshipRepository)
                .findById(menteeId);

        mentorshipService.deleteMentor(menteeId, mentorId);

        Mockito.verify(mentorshipRepository).save(Mockito.any());
    }

    @Test
    public void testDeleteMentorInvalidMentor() {
        doReturn(Optional.of(mentee))
                .when(mentorshipRepository)
                .findById(menteeId);

        Exception exception = Assert.assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentor(menteeId, userId));
        Assertions.assertEquals("Ментор не найден", exception.getMessage());
    }

    @Test
    public void testDeleteMentorInvalidUser() {
        doReturn(Optional.empty())
                .when(mentorshipRepository)
                .findById(menteeId);
        Exception exception = Assert.assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentor(menteeId, mentorId));
        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }
}
