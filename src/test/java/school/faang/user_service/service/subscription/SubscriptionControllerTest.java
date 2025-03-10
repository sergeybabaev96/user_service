package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.subscription.SubscriptionController;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.validator.subscription.SubscriptionValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @Spy
    private SubscriptionValidator subscriptionValidator;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Test
    void shouldCallServiceToFollowUserWhenIdsAreDifferent() {
        long followerId = 11;
        long followeeId = 21;

        subscriptionController.followUser(followerId, followeeId);

        verify(subscriptionService, times(1)).followUser(followerId, followeeId);
    }

    @Test
    void shouldThrowExceptionWhenFollowerIdEqualsFolloweeIdFollowMethos() {
        long followerId = 11;
        long followeeId = 11;

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                subscriptionController.followUser(followerId, followeeId)
        );

        assertEquals("You can't subscribe to yourself", exception.getMessage());
        verify(subscriptionService, Mockito.never()).followUser(anyLong(), anyLong());
    }

    @Test
    void shouldCallServiceToUnfollowUserWhenIdsAreDifferent() {
        long followerId = 11;
        long followeeId = 21;

        subscriptionController.unfollowUser(followerId, followeeId);

        verify(subscriptionService, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    void shouldThrowExceptionWhenFollowerIdEqualsFolloweeIdUnfollowMethod() {
        long followerId = 11;
        long followeeId = 11;

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                subscriptionController.unfollowUser(followerId, followeeId)
        );

        assertEquals("You can't unsubscribe from yourself", exception.getMessage());
        verify(subscriptionService, Mockito.never()).followUser(anyLong(), anyLong());
    }

    @Test
    void shouldReturnFollowersCount() {
        long followerId = 1L;
        int expectedCount = 5;

        when(subscriptionService.getFollowersCount(followerId)).thenReturn(expectedCount);

        int actualCount = subscriptionController.getFollowersCount(followerId);

        assertEquals(expectedCount, actualCount);
        verify(subscriptionService, times(1)).getFollowersCount(followerId);
    }
}
