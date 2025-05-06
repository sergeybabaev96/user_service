package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.util.DataValidationException;

import java.util.List;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserMapper userMapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.userMapper = userMapper;
    }

    public boolean isAlreadySubscribed(long followerId, long followeeId) {
        validateUserExistance(followerId, followeeId);
        return subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    @Transactional
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

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        validateUserExistance(followerId, followeeId);
        if (followerId == followeeId) {
            throw new DataValidationException("You cannot subscribe to yourself");
        } else {
            subscriptionRepository.unfollowUser(followerId, followeeId);
        }
    }

    @Transactional
    public List<UserDto> getFollowers(long followeeId, UserDtoFilter filter) {
        validateUserExistance(followeeId);
        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId)
                .filter(user -> user.getExperience() <= filter.getExperienceMax()
                                && user.getExperience() >= filter.getExperienceMin()
                                && user.getPhone().equals(filter.getPhonePattern())
                                && user.getAboutMe().contains(filter.getNamePattern()))
                .toList();
        return userMapper.mapListOfUsers(followers);
    }

    public int getFollowerCount(long followerId) {
        validateUserExistance(followerId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followerId);
    }

    @Transactional
    public List<UserDto> getFollowing(long followerId, UserDtoFilter filter) {
        validateUserExistance(followerId);
        List<User> followers = subscriptionRepository.findByFollowerId(followerId)
                .filter(user -> user.getExperience() <= filter.getExperienceMax()
                                && user.getExperience() >= filter.getExperienceMin()
                                && user.getPhone().equals(filter.getPhonePattern())
                                && user.getAboutMe().contains(filter.getNamePattern()))
                .toList();
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
            try {
                if (!subscriptionRepository.existsById(id)) {
                    System.out.println("Diag");
                }
            } catch (IllegalArgumentException e) {
                throw new DataValidationException("User with ID " + id + " does not exist");
            }
        }
    }
}
