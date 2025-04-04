package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {
    private final long followerId = 1;
    private final long followeeId = 2;

    @Mock
    private SubscriptionService subscriptionService;
    @InjectMocks
    private SubscriptionController subscriptionController;
    private UserFilterDto filterDto;

    @Test
    public void testFollow() {
        subscriptionController.followUser(followerId, followeeId);
        verify(subscriptionService, times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void testUnfollow() {
        subscriptionController.unfollowUser(followerId, followeeId);
        verify(subscriptionService, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowers() {
        UserDto firstUser = new UserDto(1, "Bob", "bob@yandex.ru", true);
        UserDto secondUser = new UserDto(2, "Ron", "ron90@gmail.com", true);
        List<UserDto> followers = Arrays.asList(firstUser, secondUser);
        when(subscriptionService.getFollowers(followeeId, filterDto)).thenReturn(followers);
        List<UserDto> result = subscriptionController.getFollowers(followeeId, filterDto);
        assertEquals(followers, result);
        verify(subscriptionService, times(1)).getFollowers(followeeId, filterDto);
    }

    @Test
    public void testFollowersCount() {
        int exceptedCount = 1;
        when(subscriptionService.getFollowersCount(followerId)).thenReturn(exceptedCount);
        int result = subscriptionController.getFollowersCount(followerId);
        assertEquals(exceptedCount, result);
        verify(subscriptionService, times(1)).getFollowersCount(followerId);
    }

    @Test
    public void testGetFollowing() {
        UserDto firstUser = new UserDto(1, "Bob", "bob@yandex.ru", true);
        UserDto secondUser = new UserDto(2, "Ron", "ron90@gmail.com", true);
        List<UserDto> following = Arrays.asList(firstUser, secondUser);
        when(subscriptionService.getFollowing(followeeId, filterDto)).thenReturn(following);
        List<UserDto> result = subscriptionController.getFollowing(followeeId, filterDto);
        assertEquals(following, result);
        verify(subscriptionService, times(1)).getFollowing(followeeId, filterDto);
    }

    @Test
    public void testFollowingCount() {
        int exceptedCount = 1;
        when(subscriptionService.getFollowingCount(followerId)).thenReturn(exceptedCount);
        int result = subscriptionController.getFollowingCount(followerId);
        assertEquals(exceptedCount, result);
        verify(subscriptionService, times(1)).getFollowingCount(followerId);
    }

}
