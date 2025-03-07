package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository repository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя подписаться на себя.");
        }
        if (repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("{} уже подписан на {}.", followerId, followeeId);
        } else {
            repository.followUser(followerId, followeeId);
            log.info("Пользователь: {} подписался на пользователя: {}.", followerId, followeeId);
        }
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя отписаться от себя.");
        } else {
            repository.unfollowUser(followerId, followeeId);
            log.info("Пользователь: {} отписался от пользователя: {}.", followerId, followeeId);
        }
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        Stream<User> followersStream = repository.findByFolloweeId(followeeId);
        return filterUsers(followersStream, filter)
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public int getFollowersCount(long followerId) {
        return repository.findFolloweesAmountByFollowerId(followerId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        Stream<User> followingStream = repository.findByFollowerId(followerId);
        return filterUsers(followingStream, filter)
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public int getFollowingCount(long followerId) {
        return repository.findFolloweesAmountByFollowerId(followerId);
    }

    private Stream<User> filterUsers(Stream<User> users, UserFilterDto filters) {
        List<UserFilter> applicableFilters = userFilters.stream()
                    .filter(filter -> filter.isApplicable(filters))
                    .toList();

        if (applicableFilters.isEmpty()) {
            return users;
        }

        for (UserFilter userFilter : applicableFilters) {
            users = userFilter.apply(users, filters);
        }

        return users;
    }
}
