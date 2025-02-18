package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.FollowerEvent;
import school.faang.user_service.dto.RecordsQuantityDto;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SubscriptionUserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionFilter;
import school.faang.user_service.service.SubscriptionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final SubscriptionRepository subscriptionRepository;
    private final List<SubscriptionFilter> subscriptionFilters;
    private final SubscriptionUserMapper subscriptionUserMapper;
    private final FollowerEventPublisher followerEventPublisher;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.error("User id={} already follow to the user id={}", followerId, followeeId);
            throw new DataValidationException("User id=" + followerId + " already follow to the user id=" + followeeId);
        }
        subscriptionRepository.followUser(followerId, followeeId);
        log.info("User id={} follow to the user id={}", followerId, followeeId);

        FollowerEvent followerEvent = new FollowerEvent(followerId, followeeId, LocalDateTime.now());

        followerEventPublisher.publish(followerEvent);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("User id={} unfollow the user id={}", followerId, followeeId);
    }

    public List<SubscriptionUserDto> getFollowers(long followeeId, SubscriptionUserFilterDto filterDto) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        return getFilteredUsers(users, filterDto);
    }

    public List<SubscriptionUserDto> getFollowing(long followeeId, SubscriptionUserFilterDto filterDto) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        return getFilteredUsers(users, filterDto);
    }

    public RecordsQuantityDto getFollowersCount(long followeeId) {
        int recordsQuantity = subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        return new RecordsQuantityDto(recordsQuantity);
    }

    public RecordsQuantityDto getFollowingCount(long followerId) {
        int recordsQuantity = subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
        return new RecordsQuantityDto(recordsQuantity);
    }

    private List<SubscriptionUserDto> getFilteredUsers(Stream<User> users, SubscriptionUserFilterDto filterDto) {
        for (SubscriptionFilter userFilter : subscriptionFilters) {
            if (userFilter.isApplicable(filterDto)) {
                users = userFilter.apply(users, filterDto);
            }
        }

        int page = calculatePage(filterDto);
        int pageSize = calculatePageSize(filterDto);

        return users
                .skip((long) (page - 1) * pageSize).limit(pageSize)
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();
    }

    private int calculatePage(SubscriptionUserFilterDto filterDto) {
        int page = filterDto.page();
        return page == 0 ? DEFAULT_PAGE_NUMBER : page;
    }

    private int calculatePageSize(SubscriptionUserFilterDto filterDto) {
        int pageSize = filterDto.pageSize();
        return pageSize == 0 ? DEFAULT_PAGE_SIZE : pageSize;
    }
}
