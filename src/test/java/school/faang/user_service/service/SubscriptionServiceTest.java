package school.faang.user_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.config.redis.SubscriptionRedisProperties;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.subscriber.MockUsers;
import school.faang.user_service.filter.subscriber.SubscriberFilter;
import school.faang.user_service.filter.subscriber.SubscriberNameFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.outbox.OutboxService;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private List<SubscriberFilter> subscriberFilters;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OutboxService outboxService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FollowerEventPublisher followerEventPublisher;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    private Long followerId = 1L;
    private Long followeeId = 2L;
    private SubscriptionRedisProperties subscriptionRedisProperties;
    private UserFilterDto filters;
    private MockUsers mockUsers = new MockUsers();

    private void mockFollowerExists(boolean exists) {
        when(userRepository.existsById(followerId)).thenReturn(exists);
    }

    private void mockFolloweeExists(boolean exists) {
        when(userRepository.existsById(followeeId)).thenReturn(exists);
    }

    @BeforeEach
    void setUp() {
        SubscriptionRedisProperties subscriptionRedisProperties = new SubscriptionRedisProperties();
        SubscriptionRedisProperties.Channel channel = new SubscriptionRedisProperties.Channel();
        channel.setFollower("someFollowerChannel");
        channel.setUnfollower("someUnfollowerChannel");
        subscriptionRedisProperties.setChannel(channel);

        filters = new UserFilterDto();

        subscriptionService = new SubscriptionService(subscriptionRepository, userRepository,
                subscriberFilters, userMapper, outboxService, objectMapper);
    }

    @Nested
    class FollowUser {

        @Test
        public void followUserFollowerIdNotFound() {
            mockFollowerExists(false);

            assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));
            verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
        }

        @Test
        public void followUserFolloweeIdNotFound() {
            mockFollowerExists(true);
            mockFolloweeExists(false);

            assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));
            verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
        }

        @Test
        public void followUserSubscriptionOnYourself() {
            mockFollowerExists(true);
            mockFollowerExists(true);

            assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followerId));
            verify(subscriptionRepository, never()).followUser(followerId, followerId);
        }

        @Test
        public void followUserAlreadySubscribed() {
            mockFollowerExists(true);
            mockFolloweeExists(true);
            when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

            assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));
            verify(subscriptionRepository, never()).followUser(followerId, followeeId);
        }

        @Test
        public void followUserSuccessful() {
            mockFollowerExists(true);
            mockFolloweeExists(true);
            when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

            subscriptionService.followUser(followerId, followeeId);

            verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);
        }
    }

    @Nested
    class UnfollowUser {

        @Test
        public void unfollowUserFollowerIdNotFound() {
            mockFollowerExists(false);

            assertThrows(DataValidationException.class, () -> subscriptionService.unfollowUser(followerId, followeeId));
            verify(subscriptionRepository, never()).unfollowUser(followerId, followeeId);
        }

        @Test
        public void unfollowUserFolloweeIdNotFound() {
            mockFollowerExists(true);
            mockFolloweeExists(false);
            mockFolloweeExists(false);

            assertThrows(DataValidationException.class, () -> subscriptionService.unfollowUser(followerId, followeeId));
            verify(subscriptionRepository, never()).unfollowUser(followerId, followeeId);
        }

        @Test
        public void unfollowUserSubscriptionOnYourself() {
            mockFollowerExists(true);
            mockFollowerExists(true);

            assertThrows(DataValidationException.class, () -> subscriptionService.unfollowUser(followerId, followerId));
            verify(subscriptionRepository, never()).unfollowUser(followerId, followerId);
        }

        @Test
        public void unfollowUserNotSubscribedUser() {
            mockFollowerExists(true);
            mockFolloweeExists(true);
            when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

            assertThrows(DataValidationException.class, () -> subscriptionService.unfollowUser(followerId, followeeId));
            verify(subscriptionRepository, never()).unfollowUser(followerId, followeeId);
        }

        @Test
        public void unfollowUserSuccessful() {
            mockFollowerExists(true);
            mockFolloweeExists(true);
            when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

            subscriptionService.unfollowUser(followerId, followeeId);

            verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
        }
    }

    @Nested
    class GetFollowers {

        @Test
        public void getFollowersFolloweeIdNotFound() {
            mockFolloweeExists(false);

            assertThrows(DataValidationException.class, () -> subscriptionService.getFollowers(followeeId, filters));
            verify(subscriptionRepository, never()).findByFolloweeId(followeeId);
            verify(userMapper, never()).toListUser(anyList());
        }

        @Test
        public void getFollowersFilterIsEmpty() {
            mockFolloweeExists(true);
            when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Stream.empty());
            when(subscriberFilters.stream()).thenReturn(Stream.empty());

            List<UserDto> usersDto = subscriptionService.getFollowers(followeeId, filters);
            assertTrue(usersDto.isEmpty());
            verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
            verify(userMapper, never()).toListUser(anyList());
        }

        @Test
        public void getFollowersSuccess() {
            mockFolloweeExists(true);
            when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(mockUsers.getUsers());
            when(subscriberFilters.stream()).thenReturn(Stream.of(new SubscriberNameFilter()));
            when(userMapper.toListUserDto(anyList())).thenReturn(List.of(new UserDto()));

            filters.setNamePattern(mockUsers.user1.getUsername());

            subscriptionService.getFollowers(followeeId, filters);
            verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
            verify(userMapper, times(1)).toListUserDto(anyList());
        }
    }

    @Nested
    class GetFollowersCount {

        @Test
        public void getFollowersCountFolloweeIdNotFound() {
            mockFolloweeExists(false);

            assertThrows(DataValidationException.class, () -> subscriptionService.getFollowersCount(followeeId));
            verify(subscriptionRepository, never()).findFollowersAmountByFolloweeId(followeeId);
        }

        @Test
        public void getFollowersCountSuccess() {
            mockFolloweeExists(true);
            
            subscriptionService.getFollowersCount(followeeId);
            verify(subscriptionRepository, times(1)).findFollowersAmountByFolloweeId(followeeId);
        }
    }

    @Nested
    class GetFollowing {

        @Test
        public void getFollowingUserIdNotFound() {
            mockFollowerExists(false);

            assertThrows(DataValidationException.class, () -> subscriptionService.getFollowing(followerId, filters));
            verify(userMapper, never()).toListUser(anyList());
        }

        @Test
        public void getFollowingFilterIsEmpty() {
            mockFollowerExists(true);
            when(subscriptionRepository.findByFollowerId(followerId)).thenReturn(Stream.empty());
            when(subscriberFilters.stream()).thenReturn(Stream.empty());

            List<UserDto> usersDto = subscriptionService.getFollowing(followerId, filters);
            assertTrue(usersDto.isEmpty());
            verify(userMapper, never()).toListUser(anyList());
        }

        @Test
        public void getFollowingSuccess() {
            mockFollowerExists(true);
            when(subscriptionRepository.findByFollowerId(followerId)).thenReturn(mockUsers.getUsers());
            when(subscriberFilters.stream()).thenReturn(Stream.of(new SubscriberNameFilter()));
            when(userMapper.toListUserDto(anyList())).thenReturn(List.of(new UserDto()));

            filters.setNamePattern(mockUsers.user1.getUsername());

            subscriptionService.getFollowing(followerId, filters);
            verify(userMapper, times(1)).toListUserDto(anyList());
        }
    }

    @Nested
    class GetFollowingCount {

        @Test
        public void getFollowingCountFollowerIdNotFound() {
            mockFollowerExists(false);

            assertThrows(DataValidationException.class, () -> subscriptionService.getFollowingCount(followerId));
            verify(subscriptionRepository, never()).findFolloweesAmountByFollowerId(followerId);
        }

        @Test
        public void getFollowingCountSuccess() {
            mockFollowerExists(true);

            subscriptionService.getFollowingCount(followerId);
            verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(followerId);
        }
    }
}
