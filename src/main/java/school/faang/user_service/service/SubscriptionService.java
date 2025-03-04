package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public void followUser(long followerId, long followeeId) {
        boolean isExistsByFollowerIdAndFolloweeId =
                subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);

        if (isExistsByFollowerIdAndFolloweeId) {
            throw new DataValidationException("Подписка на данного пользователя уже имеется.");
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        boolean isExistsByFollowerIdAndFolloweeId =
                subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (!isExistsByFollowerIdAndFolloweeId) {
            throw new DataValidationException("Нет активной подписки на пользователя.");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        return subscriptionRepository.findByFollowerId(followeeId)
                .filter(user -> filterUserByUserFilter(user, filter))
                .map(this::userToUserDto)
                .toList();
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private boolean filterUserByUserFilter(User user, UserFilterDto filter) {
        if (filter == null) {
            return true;
        }

        String namePattern = filter.getNamePattern();
        String phonePattern = filter.getPhonePattern();
        int experienceMin = filter.getExperienceMin();
        int experienceMax = filter.getExperienceMax();

        boolean isMatchByName = true;
        if (namePattern != null && !namePattern.isBlank()) {
            isMatchByName = user.getUsername().equals(namePattern);
        }

        boolean isMatchByPhone = true;
        if (phonePattern != null && !phonePattern.isBlank()) {
            isMatchByPhone = user.getPhone().equals(phonePattern);
        }

        boolean isMatchByExperience = true;
        Integer userExperience = user.getExperience();
        if (userExperience != null) {
            if (experienceMin > 0) {
                isMatchByExperience = userExperience >= experienceMin;
            }
            if (experienceMax > 0) {
                isMatchByExperience = userExperience <= experienceMax;
            }
        }

        return isMatchByName && isMatchByPhone && isMatchByExperience;
    }

    private UserDto userToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
