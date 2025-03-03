package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        boolean isExistsByFollowerIdAndFolloweeId =
                subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);

        if (isExistsByFollowerIdAndFolloweeId) {
            throw new DataValidationException("Подписка на данного пользователя уже имеется.");
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        boolean isExistsByFollowerIdAndFolloweeId =
                subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (!isExistsByFollowerIdAndFolloweeId) {
            throw new DataValidationException("Нет активной подписки на пользователя.");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }
}
