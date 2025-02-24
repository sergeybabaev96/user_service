package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.notification.UserChatIdUpdateDto;
import school.faang.user_service.dto.notification.UserNotificationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private UserRepository userRepository;
    private List<UserFilter> userFilters;
    private UserMapper userMapper;
    private UserService userService;
    private GoalService goalService;
    private EventService eventService;
    private MentorshipService mentorshipService;

    @BeforeEach
    void init() {
        userRepository = mock(UserRepository.class);
        userFilters = new ArrayList<>();
        userFilters.add(mock(UserFilter.class));
        userFilters.add(mock(UserFilter.class));
        userMapper = Mappers.getMapper(UserMapper.class);
        goalService = mock(GoalService.class);
        eventService = mock(EventService.class);
        mentorshipService = mock(MentorshipService.class);
        userService = new UserService(userRepository, userFilters,
                goalService, eventService, userMapper, mentorshipService);
    }

    @Test
    void getPremiumUsers_ShouldCorrectlyFilter() {
        User correctUser = new User();
        User wrongUser = new User();
        when(userRepository.findPremiumUsers())
                .thenReturn(Stream.of(correctUser, wrongUser));
        when(userFilters.get(0).isApplicable(any()))
                .thenReturn(true);
        when(userFilters.get(1).isApplicable(any()))
                .thenReturn(true);
        when(userFilters.get(0).apply(any(), any()))
                .thenReturn(Stream.of(correctUser, wrongUser));
        when(userFilters.get(1).apply(any(), any()))
                .thenReturn(Stream.of(correctUser));
        assertEquals(List.of(userMapper.toDto(correctUser)), userService.getPremiumUsers(new UserFilterDto()));
    }

    @Test
    void getUserDtoById_ShouldReturnUserDtoWhenUserExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(userId);

        assertEquals(userMapper.toDto(user), result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserDtoById_ShouldThrowExceptionWhenUserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserNotificationDtoById_ShouldReturnUserNotificationDtoWhenUserExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserNotificationDto result = userService.getUserNotificationDtoById(userId);

        assertEquals(userMapper.toNotificationDto(user), result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserNotificationDtoById_ShouldThrowExceptionWhenUserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserNotificationDtoById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void updateUserChatId_ShouldUpdateChatIdAndReturnUserNotificationDto() {
        Long userId = 1L;
        Long newChatId = 123456L;

        User userFromDatabase = new User();
        userFromDatabase.setId(userId);

        User userToSave = new User();
        userToSave.setId(userId);
        userToSave.setChatId(newChatId);

        UserChatIdUpdateDto updateDto = new UserChatIdUpdateDto();
        updateDto.setId(userId);
        updateDto.setChatId(newChatId);


        when(userRepository.findById(userId)).thenReturn(Optional.of(userFromDatabase));
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        UserNotificationDto result = userService.updateUserChatId(updateDto);

        assertEquals(userMapper.toNotificationDto(userToSave), result);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(userFromDatabase);
    }

    @Test
    void updateUserChatId_ShouldThrowExceptionWhenUserNotFound() {
        Long userId = 1L;
        UserChatIdUpdateDto updateDto = new UserChatIdUpdateDto();
        updateDto.setId(userId);
        updateDto.setChatId(123456L);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserChatId(updateDto));
        verify(userRepository, times(1)).findById(userId);
    }
}