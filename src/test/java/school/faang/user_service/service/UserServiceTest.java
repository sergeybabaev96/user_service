package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import org.springframework.dao.DataRetrievalFailureException;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private EventService eventService;
    @Mock private GoalService goalService;
    @Mock private MentorshipService mentorshipService;
    @Mock private UserRepository userRepository;
    @Spy private UserMapperImpl userMapper;
    @InjectMocks private UserService userService;
    long userId;

    @BeforeEach
    public void init() {
        userId = 10L;
    }

    @Test
    public void testGetUserById_UserIsFound_ReturnsUser() {var testUser = User.builder().id(userId).build();when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));var result = userService.getUserById(userId);verify(userRepository, times(1)).findById(userId);assertEquals(testUser, result);}
    @Test
    void testDeactivateUsers() {

        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setActive(true);
        User deactivatedUser = new User();
        deactivatedUser.setId(userId);
        deactivatedUser.setActive(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(deactivatedUser);
        UserDto result = userService.deactivateUser(userId);assertNotNull(result);verify(eventService).deleteEventByUserId(userId);verify(eventService).deleteParticipationFromEvent(userId);verify(goalService).deleteUserFromGoals(userId);verify(goalService).setNullInGoalsToMentor(userId);verify(mentorshipService).deleteMentorShipByDeactivatedUser(userId);verify(mentorshipService).deleteMenteeByDeactivatedUser(userId);verify(userRepository).save(any(User.class));verify(userMapper).toDto(deactivatedUser);assertFalse(deactivatedUser.isActive());
    @Test
    public void testGetUserById_UserIsNotFound_Throws() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(
                DataRetrievalFailureException.class,
                () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }
}