package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.event.FollowEvent;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.user.UserFilter;
import school.faang.user_service.mapper.SubscriptionMapper;
import school.faang.user_service.publisher.FollowMessagePublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final SubscriptionMapper subscriptionMapper;
    private final FollowMessagePublisher followMessagePublisher;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        validateFollowerAndFollowee(followerId, followeeId);
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new BusinessException("Пользователь уже подписан на данного пользователя");
        }
        subscriptionRepository.followUser(followerId, followeeId);
        log.info("Пользователь с id {} подписался на пользователя с id {}", followerId, followeeId);
        followMessagePublisher.publish(
                new FollowEvent(followerId, followeeId, LocalDateTime.now())
        );
    }

    public void unfollowUser(long followerId, long followeeId) {
        validateFollowerAndFollowee(followerId, followeeId);
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new BusinessException("Пользователь не подписан на данного пользователя");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("Пользователь с id {} отписался от пользователя с id {}", followerId, followeeId);
    }

    @Transactional
    public List<SubscriptionUserDto> getFollowers(long followeeId, UserFilterDto dto) {
        checkIdOnExist(followeeId);
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        return applyFiltersAndPagination(dto, followers);
    }

    public int getFollowersCount(Long followeeId) {
        checkIdOnExist(followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional
    public List<SubscriptionUserDto> getFollowing(long followerId, UserFilterDto dto) {
        checkIdOnExist(followerId);
        Stream<User> followers = subscriptionRepository.findByFollowerId(followerId);
        return applyFiltersAndPagination(dto, followers);
    }

    public int getFollowingCount(Long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private List<SubscriptionUserDto> applyFiltersAndPagination(UserFilterDto dto, Stream<User> followers) {
        for (var userFilter : userFilters) {
            followers = userFilter.apply(followers, dto);
        }
        int pageSize = dto.getPageSize();
        int page = dto.getPage();
        if (pageSize < 1) {
            throw new DataValidationException("Размер страницы должен быть больше 0");
        }
        if (page < 1) {
            throw new DataValidationException("Номер страницы должен быть больше 0");
        }
        int skip = (page - 1) * pageSize;
        return followers
                .skip(skip)
                .limit(pageSize)
                .map(subscriptionMapper::toDto)
                .toList();
    }

    private void validateFollowerAndFollowee(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new BusinessException("Follower и followee не могут быть одинаковыми");
        }
        checkIdOnExist(followerId);
        checkIdOnExist(followeeId);
    }

    private void checkIdOnExist(long id) {
        if (!userRepository.existsById(id)) {
            throw new DataValidationException("Пользователь с id " + id + " не найден");
        }
    }
}
