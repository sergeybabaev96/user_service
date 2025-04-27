package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.dto.publisher.FollowerEventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления подписками пользователей.
 * Содержит методы для подписки, отписки и получения списка подписчиков и подписок.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final FollowerEventPublisher followerEventPublisher;

    /**
     * Подписывает одного пользователя на другого.
     *
     * @param followerId ID пользователя, который подписывается
     * @param followeeId ID пользователя, на которого подписываются
     * @throws DataValidationException если пользователь пытается подписаться на самого себя
     *                                 или если подписка уже существует
     */
    @Transactional
    public void followUser(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            log.error("FollowerId and FolloweeId are the same: followerId={}, followeeId={}", followerId, followeeId);
            throw new DataValidationException("Cannot follow yourself");
        }

        try {
            subscriptionRepository.followUser(followerId, followeeId);
        } catch (DataIntegrityViolationException e) {
            log.error("Subscription already exists: followerId={}, followeeId={}", followerId, followeeId, e);
            throw new DataValidationException("Subscription already exists");
        }

        FollowerEventDto event = new FollowerEventDto(followerId, followeeId, LocalDateTime.now());
        followerEventPublisher.publish(event);
    }

    /**
     * Отписывает одного пользователя от другого.
     *
     * @param followerId ID пользователя, который отписывается
     * @param followeeId ID пользователя, от которого отписываются
     * @throws DataValidationException если пользователь пытается отписаться от самого себя
     */
    @Transactional
    public void unfollowUser(Long followerId, Long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Cannot unfollow yourself");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    /**
     * Получает список подписчиков пользователя с возможностью фильтрации.
     *
     * @param followeeId ID пользователя, для которого ищутся подписчики
     * @param filter     фильтр для отбора пользователей
     * @return список пользователей, подписанных на указанного пользователя
     */
    @Transactional(readOnly = true)
    public List<UserViewDto> getFollowers(Long followeeId, UserFilterDto filter) {
        return subscriptionRepository.findByFolloweeId(followeeId)
                .map(userMapper::toViewDto)
                .filter(user -> matchesFilter(user, filter))
                .collect(Collectors.toList());
    }

    /**
     * Получает список пользователей, на которых подписан указанный пользователь, с возможностью фильтрации.
     *
     * @param followerId ID пользователя, чьи подписки возвращаются
     * @param filter     фильтр для отбора пользователей
     * @return список пользователей, на которых подписан указанный пользователь
     */
    @Transactional(readOnly = true)
    public List<UserViewDto> getFollowing(Long followerId, UserFilterDto filter) {
        return subscriptionRepository.findByFollowerId(followerId)
                .map(userMapper::toViewDto)
                .filter(user -> matchesFilter(user, filter))
                .collect(Collectors.toList());
    }

    /**
     * Получает количество подписчиков у пользователя.
     *
     * @param followeeId ID пользователя
     * @return количество подписчиков
     */
    public Integer getFollowersCount(Long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    /**
     * Получает количество пользователей, на которых подписан указанный пользователь.
     *
     * @param followerId ID пользователя
     * @return количество подписок
     */
    public Integer getFollowingCount(Long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    /**
     * Проверяет, соответствует ли пользователь фильтру.
     *
     * @param user   пользователь для проверки
     * @param filter параметры фильтрации
     * @return {@code true}, если пользователь соответствует фильтру; иначе {@code false}
     */
    private boolean matchesFilter(UserViewDto user, UserFilterDto filter) {
        if (filter == null) {
            return true;
        }

        boolean matches = true;

        if (filter.getNamePattern() != null && user.getUsername() != null) {
            matches &= user.getUsername().toLowerCase().contains(filter.getNamePattern().toLowerCase());
        }

        if (filter.getPhonePattern() != null && user.getPhone() != null) {
            matches &= user.getPhone().contains(filter.getPhonePattern());
        }

        Integer experience = user.getExperience();
        if (experience != null) {
            if (filter.getExperienceMin() > 0) {
                matches &= experience >= filter.getExperienceMin();
            }
            if (filter.getExperienceMax() > 0) {
                matches &= experience <= filter.getExperienceMax();
            }
        }

        return matches;
    }
}