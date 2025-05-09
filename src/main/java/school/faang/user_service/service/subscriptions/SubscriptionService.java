package school.faang.user_service.service.subscriptions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.subscriptions.SubscriptionFilter;
import school.faang.user_service.mappers.user.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validation.subscriptions.SubscriptionValidations;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<SubscriptionFilter> subscriptionFilters;
    private final SubscriptionValidations subscriptionValidations;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        boolean existSub = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        subscriptionValidations.ValidateFollowAction(followerId, followeeId);
        subscriptionValidations.ValidateSubscribeAction(existSub);

        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        boolean existSub = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        subscriptionValidations.ValidateUnfollowAction(followerId, followeeId);
        subscriptionValidations.ValidateUnsubscribeAction(existSub);

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
