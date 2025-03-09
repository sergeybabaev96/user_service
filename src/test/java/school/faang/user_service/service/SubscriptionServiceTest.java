package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    private final long followerId = 1;
    private final long followeeId = 2;

    @Mock
    private SubscriptionRepository repository;

    @Mock
    private List<UserFilter> userFilters;

    @Mock
    private UserFilter userFilter;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    SubscriptionService subscriptionService;
    UserFilterDto filterDto;

    @Test
    public void testFollowYourSelf() {
        assertThrows(DataValidationException.class, () ->
                subscriptionService.followUser(followerId, followerId));
    }

    @Test
    public void testFollowTwice() {
        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);
        assertThrows(DataValidationException.class, () ->
                subscriptionService.followUser(followerId, followeeId));
    }

    @Test
    public void testFollow() {
        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);
        subscriptionService.followUser(followerId, followeeId);
        verify(repository, times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowYourself() {
        assertThrows(DataValidationException.class, () ->
                subscriptionService.unfollowUser(followerId, followerId));
    }

    @Test
    public void testUnfollowFromUnfollowingUser() {
        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);
        assertThrows(DataValidationException.class, () ->
                subscriptionService.unfollowUser(followerId, followeeId));
    }

    @Test
    public void testUnfollow() {
        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);
        subscriptionService.unfollowUser(followerId, followeeId);
        verify(repository, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowers() {
        User follower1 = User.builder().username("Robert").build();
        User follower2 = User.builder().username("+79209202495").build();
        when(repository.findByFolloweeId(followeeId)).thenReturn(Stream.of(follower1, follower2));
        UserFilterDto userFilterDto = new UserFilterDto("Rob", null, 0, 0);
        when(userFilters.stream()).thenReturn(Stream.of(userFilter));
        when(userFilter.isApplicable(userFilterDto)).thenReturn(true);
        when(userFilter.apply(any(Stream.class), eq(userFilterDto)))
                .thenAnswer(invocation -> {
                    Stream<User> inputStream = invocation.getArgument(0);
                    return inputStream.filter(user ->
                            user.getUsername() != null
                                    &&
                                    user.getUsername().contains("Rob")
                    );
                });
        UserDto dtoRobert = new UserDto(5L, "Robert", "robN@gmail.com");
        when(userMapper.toDto(follower1)).thenReturn(dtoRobert);
        List<UserDto> result = subscriptionService.getFollowers(followeeId, userFilterDto);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Robert", result.get(0).username());
        verify(repository, times(1)).findByFolloweeId(followeeId);
    }

    @Test
    public void testGetFollowersCount() {
        int followersCount = 5;
        when(repository.findFolloweesAmountByFollowerId(followerId)).thenReturn(followersCount);
        int followersCountResult = subscriptionService.getFollowersCount(followerId);
        assertEquals(followersCount, followersCountResult);
        verify(repository, times(1)).findFolloweesAmountByFollowerId(followerId);
    }

    @Test
    public void testGetFollowing() {
        User followee1 = User.builder().username("Alice").build();
        User followee2 = User.builder().username("Anna").build();
        when(repository.findByFollowerId(followerId)).thenReturn(Stream.of(followee1, followee2));
        UserFilterDto userFilterDto = new UserFilterDto("al", null, 0, 0);
        when(userFilters.stream()).thenReturn(Stream.of(userFilter));
        when(userFilter.isApplicable(userFilterDto)).thenReturn(true);
        when(userFilter.apply(any(Stream.class), eq(userFilterDto)))
                .thenAnswer(invocation -> {
                    Stream<User> inputStream = invocation.getArgument(0);
                    return inputStream.filter(user ->
                            user.getUsername() != null
                                    &&
                                    user.getUsername().toLowerCase().contains("al")
                    );
                });

        UserDto dtoAlice = new UserDto(1L, "Alice", "qHl8N@example.com");
        when(userMapper.toDto(followee1)).thenReturn(dtoAlice);
        List<UserDto> result = subscriptionService.getFollowing(followerId, userFilterDto);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).username());
        verify(repository, times(1)).findByFollowerId(followerId);
    }

    @Test
    public void testGetFollowingCount() {
        int followingsCount = 5;
        when(repository.findFolloweesAmountByFollowerId(followerId)).thenReturn(followingsCount);
        int followingsCountResult = subscriptionService.getFollowingCount(followerId);
        assertEquals(followingsCount, followingsCountResult);
        verify(repository, times(1)).findFolloweesAmountByFollowerId(followerId);
    }
}
