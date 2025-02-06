package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private UserRepository userRepository;
    private List<UserFilter> userFilters;
    private UserMapperImpl userMapper;
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
        userMapper = spy(UserMapperImpl.class);
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
}