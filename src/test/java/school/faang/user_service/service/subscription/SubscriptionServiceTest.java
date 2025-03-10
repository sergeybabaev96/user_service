package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.user.filter.UserFilter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserFilter userFilter;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private UserFilterDto filterDto;
    private User firstUser;
    private User secondUser;

    @Test
    public void shouldFollowUserWhenNotAlreadySubscribed() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);
        subscriptionService.followUser(followerId, followeeId);
        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionWhenAlreadySubscribed() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                subscriptionService.followUser(followerId, followeeId)
        );

        assertEquals("This subscriber already exists", exception.getMessage());
        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    public void shouldUnfollowUserWhenSubscribed() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);
        subscriptionService.unfollowUser(followerId, followeeId);
        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionWhenUnsubscribed() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                subscriptionService.unfollowUser(followerId, followeeId)
        );

        assertEquals("You are not subscribed to this user", exception.getMessage());
        verify(subscriptionRepository, never()).unfollowUser(anyLong(), anyLong());
    }

    @Test
    public void shouldReturnFollowersCount() {
        long followerId = 1L;
        int expectedCount = 5;

        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(expectedCount);

        int actualCount = subscriptionService.getFollowersCount(followerId);

        assertEquals(expectedCount, actualCount);
        verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(followerId);
    }

    @BeforeEach
    void setUp() {
        filterDto = new UserFilterDto();
        filterDto.setNamePattern("JohnDoe");
        filterDto.setEmailPattern("johndoe@example.com");
        filterDto.setCityPattern("New York");
        filterDto.setExperienceMin(1);
        filterDto.setExperienceMax(10);
        filterDto.setPage(0);
        filterDto.setPageSize(10);

        firstUser = new User();
        firstUser.setUsername("JohnDoe");
        firstUser.setEmail("johndoe@example.com");
        firstUser.setCity("New York");
        firstUser.setExperience(2);

        secondUser = new User();
        secondUser.setUsername("JaneSmith");
        secondUser.setEmail("janesmith@example.com");
        secondUser.setCity("London");
        secondUser.setExperience(5);

        List<UserFilter> userFilters = List.of(userFilter);
        subscriptionService = new SubscriptionService(subscriptionRepository, userFilters);

    }

    @Test
    void shouldFilterFollowersCorrectly() {

        Stream<User> followers = Stream.of(firstUser, secondUser);
        when(userFilter.isApplicable(filterDto)).thenReturn(true);
        when(userFilter.apply(followers, filterDto))
                .thenAnswer(invocation -> List.of(firstUser, secondUser));

        List<User> filteredUsers = subscriptionService.filterUsers(followers, filterDto);

        assertEquals(2, filteredUsers.size());
        assertEquals("JohnDoe", filteredUsers.get(0).getUsername());
        verify(userFilter, times(1)).apply(any(), any());
    }

    @Test
    void shouldReturnFollowers() {
        long followeeId = 1L;
        Stream<User> userStream = Stream.of(firstUser);

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(userStream);
        when(userFilter.isApplicable(filterDto)).thenReturn(true);
        when(userFilter.apply(userStream, filterDto))
                .thenAnswer(invocation -> List.of(firstUser, secondUser));
        when(subscriptionService.filterUsers(userStream, filterDto)).thenReturn(userStream.collect(Collectors.toList()));

        List<User> result = subscriptionService.getFollowers(followeeId, filterDto);

        assertEquals(1, result.size());
        assertEquals("JohnDoe", result.get(0).getUsername());
        assertEquals("johndoe@example.com", result.get(0).getEmail());
        verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
    }

    @Test
    void shouldReturnEmptyListWhenNoFollowers() {
        long followeeId = 1L;

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Stream.empty());
        List<User> result = subscriptionService.getFollowers(followeeId, filterDto);

        assertEquals(0, result.size());
        verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
    }

    @Test
    void shouldReturnLimitedUsersWhenPageSizeIsMaximum() {
        long followeeId = 1L;
        filterDto.setPageSize(Integer.MAX_VALUE);
        filterDto = new UserFilterDto();

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Stream.of(firstUser, secondUser));
        when(userFilter.isApplicable(filterDto)).thenReturn(true);
        when(userFilter.apply(any(), any())).thenAnswer(invocation -> List.of(firstUser, secondUser));

        List<User> result = subscriptionService.getFollowers(followeeId, filterDto);

        assertEquals(2, result.size());
        verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
    }
}