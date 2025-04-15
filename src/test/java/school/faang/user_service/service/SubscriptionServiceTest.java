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
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    private FollowerEventPublisher followerEventPublisher;

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
        Stream<User> followers = Stream.of(new User());
        when(repository.findByFolloweeId(followeeId)).thenReturn(followers);

        List<UserDto> result = subscriptionService.getFollowers(followeeId, filterDto);

        assertNotNull(result);
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
    public void testGettingFollowing() {
        Stream<User> following = Stream.of(new User());
        when(repository.findByFollowerId(followerId)).thenReturn(following);

        List<UserDto> result = subscriptionService.getFollowing(followerId, filterDto);

        assertNotNull(result);
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
