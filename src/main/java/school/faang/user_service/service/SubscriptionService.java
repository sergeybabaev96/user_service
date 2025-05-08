package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.subscriptions.SubscriptionFilter;
import school.faang.user_service.mappers.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<SubscriptionFilter> subscriptionFilters;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        boolean existSub = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);

        if (existSub) {
            throw new DataValidationException("You are already subscribed to this user");
        }

        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        boolean existSub = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);

        if (!existSub) {
            throw new DataValidationException("You cant unsubscribe to user that you are not subscribed");
        }

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filterDto) {
        Stream<User> userStream = subscriptionRepository.findByFolloweeId(followeeId);

        return filterUsers(userStream, filterDto);
    }

    @Transactional(readOnly = true)
    public Integer getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowing(long followeeId, UserFilterDto filterDto) {
        Stream<User> userStream = subscriptionRepository.findByFollowerId(followeeId);

        return filterUsers(userStream, filterDto);
    }

    @Transactional(readOnly = true)
    public Integer getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private List<SubscriptionFilter> getApplicableFilters(UserFilterDto filterDto) {
        return subscriptionFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .toList();
    }

    private boolean applyFilters(User user, UserFilterDto filterDto) {
        List<SubscriptionFilter> filters = getApplicableFilters(filterDto);

        return filters.stream().allMatch(filter -> filter.apply(user, filterDto));
    }

    private List<UserDto> filterUsers(Stream<User> users, UserFilterDto filterDto) {
        return users
                .filter((user) -> applyFilters(user, filterDto))
                .map(userMapper::userToUserDTO)
                .toList();
    }


}
