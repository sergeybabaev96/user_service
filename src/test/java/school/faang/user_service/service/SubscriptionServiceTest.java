package school.faang.user_service.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.subscriber.*;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SubscriberMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    private final long FOLLOWER_ID = 1L;
    private final long FOLLOWEE_ID = 2L;

    @Mock
    private SubscriptionRepository repository;

    @Spy
    private SubscriberMapperImpl mapper;

    @InjectMocks
    private SubscriptionService service;

    private SubscriberFilterDto filtersDto;

    @BeforeEach
    void setUp() {
        service = new SubscriptionService(repository, List.of(), mapper);
        filtersDto = new SubscriberFilterDto();
    }

    @Test
    public void testSubscriptionForYourself() {
        assertThrows(DataValidationException.class, () -> service.followUser(FOLLOWER_ID, FOLLOWER_ID));
    }

    @Test
    public void testRepeatSubscriptionForAnotherUser() {
        when(repository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID)).thenReturn(true);
        assertThrows(DataValidationException.class, () -> service.followUser(FOLLOWER_ID, FOLLOWEE_ID));
    }

    @Test
    public void testSubscriptionForAnotherUser() {
        when(repository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID)).thenReturn(false);
        service.followUser(FOLLOWER_ID, FOLLOWEE_ID);
        verify(repository, times(1)).followUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testUnsubscribingFromYourself() {
        assertThrows(DataValidationException.class, () -> service.unfollowUser(FOLLOWER_ID, FOLLOWER_ID));
    }

    @Test
    public void testUnsubscribingFromAnUnsignedUser() {
        when(repository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID)).thenReturn(false);
        assertThrows(DataValidationException.class, () -> service.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID));
    }

    @Test
    public void testUnsubscriptionFromAnotherUser() {
        when(repository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID)).thenReturn(true);
        service.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
        verify(repository, times(1)).unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testGettingFollowers() {
        Stream<User> followers = Stream.of(new User());
        when(repository.findByFolloweeId(FOLLOWEE_ID)).thenReturn(followers);

        List<SubscriberReadDto> result = service.getFollowers(FOLLOWEE_ID, filtersDto);

        assertNotNull(result);
        verify(repository, times(1)).findByFolloweeId(FOLLOWEE_ID);
    }

    @Test
    public void testGettingFollowersCount() {
        int followersCount = 8;
        when(repository.findFollowersAmountByFolloweeId(FOLLOWEE_ID)).thenReturn(followersCount);

        int followersCountResult = service.getFollowersCount(FOLLOWEE_ID);

        assertEquals(followersCount, followersCountResult);
        verify(repository, times(1)).findFollowersAmountByFolloweeId(FOLLOWEE_ID);
    }

    @Test
    public void testGettingFollowing() {
        Stream<User> following = Stream.of(new User());
        when(repository.findByFollowerId(FOLLOWER_ID)).thenReturn(following);

        List<SubscriberReadDto> result = service.getFollowing(FOLLOWER_ID, filtersDto);

        assertNotNull(result);
        verify(repository, times(1)).findByFollowerId(FOLLOWER_ID);
    }

    @Test
    public void testGettingFollowingCount() {
        int followingCount = 7;
        when(repository.findFolloweesAmountByFollowerId(FOLLOWER_ID)).thenReturn(followingCount);

        int followingCountResult = service.getFollowingCount(FOLLOWER_ID);

        assertEquals(followingCount, followingCountResult);
        verify(repository, times(1)).findFolloweesAmountByFollowerId(FOLLOWER_ID);
    }
}