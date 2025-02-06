package school.faang.user_service.service;
/*
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) */
public class SubscriptionServiceTest {
    /*
    @InjectMocks
    private SubscriptionService service;

    @Mock
    private SubscriptionRepository repository;

    @Test
    void testSubscriptionForYourself() {
        long followerId = 1L;
        long followeeId = 1L;
        assertThrows(DataValidationException.class, () -> service.followUser(followerId, followeeId));
    }

    @Test
    void testRepeatSubscriptionForAnotherUser() {
        InitialData data = getData(true);
        assertThrows(DataValidationException.class, () -> service.followUser(data.followerId(), data.followeeId()));
    }

    @Test
    void testSubscriptionForAnotherUser() {
        InitialData data = getData(false);
        service.followUser(data.followerId(), data.followeeId());
        verify(repository, times(1)).followUser(data.followerId(), data.followeeId());
    }

    @Test
    void testUnsubscribingFromYourself() {
        long followerId = 1L;
        long followeeId = 1L;
        assertThrows(DataValidationException.class, () -> service.unfollowUser(followerId, followeeId));
    }

    @Test
    void testNonExistingUnsubscriptionFromAnotherUser() {
        InitialData data = getData(false);
        assertThrows(DataValidationException.class, () -> service.unfollowUser(data.followerId(), data.followeeId()));
    }

    @Test
    void testUnsubscriptionFromAnotherUser() {
        InitialData data = getData(true);
        service.unfollowUser(data.followerId(), data.followeeId());
        verify(repository, times(1)).unfollowUser(data.followerId(), data.followeeId());
    }

    private @NotNull InitialData getData(boolean isThereSub) {
        long followerId = 1L;
        long followeeId = 2L;
        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(isThereSub);
        return new InitialData(followerId, followeeId);
    }

    private record InitialData(long followerId, long followeeId) {
    } */
}