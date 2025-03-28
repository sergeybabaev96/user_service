package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository repository;
    @InjectMocks
    private SubscriptionService service;

    @Test
    public void testFollowUser() {
        long followerId = 1L;
        long followeeId = 2L;

        service.followUser(followerId, followeeId);
        verify(repository).followUser(followerId, followeeId);
    }

    @Test
    public void testThrowExceptionFollowUserThemselves() {
        long id = 1L;

        assertThrows(DataValidationException.class, () -> {
            service.followUser(id, id);
        });
    }

    @Test
    public void testThrowExceptionFollowUserByExistingFollowing() {
        long followerId = 1L;
        long followeeId = 2L;
        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> {
            service.followUser(followerId, followeeId);
        });
    }

    @Test
    public void testUnfollowUser() {
        long followerId = 1L;
        long followeeId = 2L;

        service.unfollowUser(followerId, followeeId);
        verify(repository).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testThrowExceptionUnfollowUserThemselves() {
        long id = 1L;

        assertThrows(DataValidationException.class, () -> {
            service.followUser(id, id);
        });
    }

    @Test
    public void testThrowExceptionUnfollowUserByNoExistingFollowing() {
        long followerId = 1L;
        long followeeId = 2L;
        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> {
            service.followUser(followerId, followeeId);
        });
    }

    @Test
    public void testGetFollowersCount() {
        long id = 1L;

        service.getFollowingCount(id);
        verify(repository).findFolloweesAmountByFollowerId(id);
    }

    @Test
    public void testGetFollowingCount() {
        long id = 1L;

        service.getFollowingCount(id);
        verify(repository).findFollowersAmountByFolloweeId(id);
    }
}