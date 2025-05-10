package school.faang.user_service.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.validation.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.serviceImpl.subscription_filters.UserFilterCombination;
import school.faang.user_service.serviceImpl.subscription_filters.UserFilterStrategy;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final List<UserFilterStrategy> filterStrategies = List.of(
            (user, userDtoFilter) -> user.getExperience() <= userDtoFilter.getExperienceMax(),
            (user, userDtoFilter) -> user.getExperience() >= userDtoFilter.getExperienceMin(),
            (user, userDtoFilter) -> user.getAboutMe().contains(userDtoFilter.getNamePattern()),
            (user, userDtoFilter) -> user.getPhone().equals(userDtoFilter.getPhonePattern())
    );
    private final UserFilterCombination filters = new UserFilterCombination(filterStrategies);

    private boolean isAlreadySubscribed(long followerId, long followeeId) {
        validateUserExistance(followerId, followeeId);
        return subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    public void followUser(long followerId, long followeeId) {
        validateUserExistance(followerId, followeeId);
        if (followerId == followeeId) {
            throw new DataValidationException("You cannot subscribe to yourself");
        }
        if (isAlreadySubscribed(followerId, followeeId)) {
            throw new DataValidationException("You are already subscribed to this user");
        } else {
            subscriptionRepository.followUser(followerId, followeeId);
        }
    }

    public void unfollowUser(long followerId, long followeeId) {
        validateUserExistance(followerId, followeeId);
        if (followerId == followeeId) {
            throw new DataValidationException("You cannot subscribe to yourself");
        } else {
            subscriptionRepository.unfollowUser(followerId, followeeId);
        }
    }

    public List<UserDto> getFollowers(long followeeId, UserDtoFilter userDtoFilter) {
        validateUserExistance(followeeId);
        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId)
                .filter(user -> filters.filterUser(user, userDtoFilter))
                .toList();
        if (followers.isEmpty()) {
            throw new DataValidationException("No followers found matching the criteria");
        }
        return userMapper.mapListOfUsers(followers);
    }

    public int getFollowerCount(long followerId) {
        validateUserExistance(followerId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followerId);
    }

    public List<UserDto> getFollowing(long followerId, UserDtoFilter userDtoFilter) {
        validateUserExistance(followerId);
        List<User> followers = subscriptionRepository.findByFollowerId(followerId)
                .filter(user -> filters.filterUser(user, userDtoFilter))
                .toList();
        if (followers.isEmpty()) {
            throw new DataValidationException("Not following anyone matching the criteria");
        }
        return userMapper.mapListOfUsers(followers);
    }

    public int getFollowingCount(long followerId) {
        validateUserExistance(followerId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    @Mapper(componentModel = "spring")
    public interface UserMapper {
        List<UserDto> mapListOfUsers(List<User> subscriptions);
    }

    private void validateUserExistance(long... ids) {
        if (ids == null || ids.length == 0) {
            throw new DataValidationException("Id must not be null or empty");
        }
        for (long id : ids) {
            if (!userRepository.existsById(id)) {
                throw new DataValidationException("User with ID " + id + " does not exist");
            }
        }
    }
}
