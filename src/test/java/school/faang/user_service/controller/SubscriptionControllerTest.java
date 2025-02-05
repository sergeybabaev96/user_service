package school.faang.user_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.impl.SubscriptionServiceImpl;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @Mock
    private SubscriptionServiceImpl subscriptionService;
    @Mock
    private HttpServletRequest request;
    @InjectMocks
    private SubscriptionController subscriptionController;
    private SubscriptionUserFilterDto subscriptionUserEmptyFilterDto;
    private long followerId;
    private long followeeId;

    @BeforeEach
    public void init() {
        followerId = 1L;
        followeeId = 2L;

        subscriptionUserEmptyFilterDto = SubscriptionUserFilterDto.builder().build();
    }

    @Test
    @DisplayName("Follow To Another User")
    void testFollowUserToAnotherUser() {
        when(request.getRequestURL()).thenReturn(new StringBuffer().append("/api/v1/subscription/some_endpoint"));
        subscriptionController.followUser(followerId, followeeId, request);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .followUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Follow To Himself")
    void testFollowUserByHimself() {
        when(request.getRequestURL()).thenReturn(new StringBuffer().append("/api/v1/subscription/some_endpoint"));
        Assert.assertThrows(DataValidationException.class,
                () -> subscriptionController.followUser(followerId, followerId, request));
    }

    @Test
    @DisplayName("Unfollow Another User")
    void testUnfollowUserFromAnotherUser() {
        when(request.getRequestURL()).thenReturn(new StringBuffer().append("/api/v1/subscription/some_endpoint"));
        subscriptionController.unfollowUser(followerId, followeeId, request);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Unfollow User Himself")
    void testUnfollowUserByHimself() {
        when(request.getRequestURL()).thenReturn(new StringBuffer().append("/api/v1/subscription/some_endpoint"));
        Assert.assertThrows(DataValidationException.class,
                () -> subscriptionController.unfollowUser(followerId, followerId, request));
    }

    @Test
    @DisplayName("Get All Followers")
    void testGetAllFollowers() {
        subscriptionController.getFollowers(followerId, subscriptionUserEmptyFilterDto);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowers(followerId, subscriptionUserEmptyFilterDto);
    }

    @Test
    @DisplayName("Get Followers Count")
    void testGetFollowersCount() {
        subscriptionController.getFollowersCount(followeeId);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowersCount(followeeId);
    }

    @Test
    @DisplayName("Get All Followees")
    void testGetAllFollowees() {
        subscriptionController.getFollowing(followeeId, subscriptionUserEmptyFilterDto);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowing(followeeId, subscriptionUserEmptyFilterDto);
    }

    @Test
    @DisplayName("Get Following Count")
    void testGetFollowingCount() {
        subscriptionController.getFollowingCount(followerId);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowingCount(followerId);
    }
}