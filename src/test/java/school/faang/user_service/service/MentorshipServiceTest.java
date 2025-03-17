package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private UserService userService;

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

        List<User> mentees = new ArrayList<>();
        mentees.add(mentee);
        mentor.setMentees(mentees);

        List<User> mentors = new ArrayList<>();
        mentors.add(mentor);
        mentee.setMentors(mentors);

        user.setMentees(mentees);
        user.setMentors(mentors);
    }

    @Test
    @DisplayName("Получение менти пользователя (позитивный сценарий)")
    public void testGetMenteesPositive() {
        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        doReturn(new UserViewDto())
                .when(userMapper)
                .toViewDto(mentee);

        List<UserViewDto> result = mentorshipService.getMentees(user.getId());
        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Проверка получения пустого списка менти")
    public void testGetMenteesEmptyList() {
        user.setMentees(new ArrayList<>());
        Mockito.when(userService.getUser(userId))
                .thenReturn(user);

        List<UserViewDto> result = mentorshipService.getMentees(user.getId());
        Assertions.assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Получение менторов пользователя (позитивный сценарий)")
    public void testGetMentorsPositive() {
        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        doReturn(new UserViewDto())
                .when(userMapper)
                .toViewDto(mentor);

        List<UserViewDto> result = mentorshipService.getMentors(user.getId());
        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Проверка получения пустого списка менторов")
    public void testGetMentorsEmptyList() {
        user.setMentors(new ArrayList<>());
        Mockito.when(userService.getUser(userId))
                .thenReturn(user);

        List<UserViewDto> result = mentorshipService.getMentors(user.getId());
        Assertions.assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Успешное удаление менти у ментора")
    public void testDeleteMenteeSuccess() {
        Mockito.when(userService.getUser(mentorId))
                .thenReturn(mentor);

        mentorshipService.deleteMentee(menteeId, mentorId);

        Mockito.verify(mentorshipRepository).save(Mockito.any());
    }

    @Test
    @DisplayName("Удаление несуществующего менти вызывает исключение")
    public void testDeleteMenteeInvalidMentee() {
        Mockito.when(userService.getUser(mentorId))
                .thenReturn(mentor);

        Exception exception = Assert.assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentee(userId, mentorId));
        Assertions.assertEquals("Менти не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Успешное удаление ментора у менти")
    public void testDeleteMentorSuccess() {
        Mockito.when(userService.getUser(menteeId))
                .thenReturn(mentee);

        mentorshipService.deleteMentor(menteeId, mentorId);

        Mockito.verify(mentorshipRepository).save(Mockito.any());
    }

    @Test
    @DisplayName("Удаление несуществующего ментора вызывает исключение")
    public void testDeleteMentorInvalidMentor() {
        Mockito.when(userService.getUser(menteeId))
                .thenReturn(mentee);

        Exception exception = Assert.assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentor(menteeId, userId));
        Assertions.assertEquals("Ментор не найден", exception.getMessage());
    }
}
