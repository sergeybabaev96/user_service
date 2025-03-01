package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.FollowerEvent;
import school.faang.user_service.dto.FollowingFeatureDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.UserWasNotFoundException;
import school.faang.user_service.mapper.UserFollowingMapper;
import school.faang.user_service.rating.ActionType;
import school.faang.user_service.rating.publisher.FollowerEventPublisher;
import school.faang.user_service.rating.publisher.UserEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.filter.UserFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserEventPublisher userEventPublisher;
    private final UserFollowingMapper userFollowingMapper;
    private final List<UserFilter> filters;
    private final FollowerEventPublisher followerEventPublisher;

    public long getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public long getFollowingCount(long followeeId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followeeId);
    }

    @Transactional
    public List<UserDto> getFollowees(long followeeId, UserFilterDto userFilterDto) {
        Stream<User> followersOfUser = subscriptionRepository.findByFolloweeId(followeeId);
        return filterPeople(followersOfUser, userFilterDto);
    }

    @Transactional
    public List<UserDto> getFollowers(long followeeId, UserFilterDto userFilterDto) {
        Stream<User> followersOfUser = subscriptionRepository.findByFollowerId(followeeId);
        return filterPeople(followersOfUser, userFilterDto);
    }

    private List<UserDto> filterPeople(Stream<User> followersOfUser, UserFilterDto userFilterDto) {
        if (filters == null || filters.isEmpty()) {
            return filterOnlyLimitsSkips(followersOfUser, userFilterDto);
        }

        Stream<User> userStream = followersOfUser.parallel();
        for (UserFilter filter : filters) {
            if (filter.isAcceptable(userFilterDto)) {
                userStream = filter.accept(userStream, userFilterDto);
            }
        }
        return filterOnlyLimitsSkips(followersOfUser, userFilterDto);
    }

    public void followUser(FollowingFeatureDto followingFeatureDto) {
        long followerId = followingFeatureDto.followerId();
        long followeeId = followingFeatureDto.followeeId();

        log.info("Trying to follow to user! : {} -> {}", followerId, followeeId);
        checkIfOneUser(followerId, followeeId);

        User requestUser = findUserById(followerId);
        User requestedUser = findUserById(followeeId);
        List<User> followedUsers = requestUser.getFollowers();
        List<User> followingUsers = requestedUser.getFollowees();

        if (existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.error("The user already followed! : {} -> {}", followerId, followeeId);
            throw new DataValidationException("User already followed!");
        }

        followedUsers.add(requestedUser);
        followingUsers.add(requestUser);

        userRepository.save(requestUser);
        userRepository.save(requestedUser);

        userEventPublisher.publishEvent(ActionType.FOLLOW, followeeId);
        followerEventPublisher.publish(new FollowerEvent(followerId, followeeId, null, LocalDateTime.now()));
        log.info("Succeed of following user!");
    }

    public void unfollowUser(FollowingFeatureDto followingFeatureDto) {
        long followerId = followingFeatureDto.followerId();
        long followeeId = followingFeatureDto.followeeId();

        log.info("Trying to unfollow to user! : {} -> {}", followerId, followeeId);
        checkIfOneUser(followerId, followeeId);
        isFollowingUserNotFollower(followerId, followeeId);

        User requestUser = findUserById(followerId);
        User requestedUser = findUserById(followeeId);
        List<User> followedUsers = requestUser.getFollowees();
        List<User> followingUsers = requestedUser.getFollowers();

        followedUsers.remove(requestedUser);
        followingUsers.remove(requestUser);

        userRepository.save(requestedUser);
        userRepository.save(requestedUser);

        userEventPublisher.publishEvent(ActionType.UNFOLLOW, followeeId);

        log.info("Succeed of unfollowing user!");
    }

    private void isFollowingUserNotFollower(long followerId, long followeeId) {
        if (!findUserById(followerId).getFollowees().contains(findUserById(followeeId))) {
            log.error("The followerId is not following followeeId : {} -> {}", followerId, followeeId);
            throw new DataValidationException("Trying to follow to person not followed!");
        }
    }

    private boolean existsByFollowerIdAndFolloweeId(long followerId, long followeeId) {
        User requestUser = findUserById(followerId);
        User requestedUser = findUserById(followeeId);

        log.info("Checked existing by follower and followee!");
        return requestUser.getFollowers().stream()
                .anyMatch(follower -> Objects.equals(follower.getId(), requestedUser.getId()));
    }

    private User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserWasNotFoundException("User was not found with id : " + id));
    }

    private void checkIfOneUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            log.error("The followerId & followeeId is equals : {} -> {}", followerId, followeeId);
            throw new DataValidationException("Trying to follow to yourself!");
        }
    }

    private List<UserDto> filterOnlyLimitsSkips(Stream<User> followers, UserFilterDto userFilterDto) {
        return followers
                .skip((long) userFilterDto.page() * userFilterDto.pageSize())
                .limit(userFilterDto.pageSize())
                .map(userFollowingMapper::toDto)
                .filter(Objects::nonNull)
                .toList();
    }
}
