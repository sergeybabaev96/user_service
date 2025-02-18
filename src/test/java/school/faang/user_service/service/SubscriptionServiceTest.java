package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SubscriptionUserMapper;
import school.faang.user_service.mapper.SubscriptionUserMapperImpl;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.impl.*;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    private SubscriptionRepository subscriptionRepositoryMock;
    private SubscriptionService subscriptionService;
    @Spy
    SubscriptionUserMapper mapper = new SubscriptionUserMapperImpl();

    private FollowerEventPublisher followerEventPublisher;

    private long followerId;
    private long followeeId;
    private List<User> allUsers;

    @BeforeEach
    public void init() {
        followerId = 1L;
        followeeId = 2L;
        allUsers = TestData.getUsers();
        List<SubscriptionFilter> filters = generateFilters();

        subscriptionRepositoryMock = Mockito.mock(SubscriptionRepository.class);
        //SubscriptionFilter filterMock = Mockito.mock(SubscriptionFilter.class);
        subscriptionService = new SubscriptionServiceImpl(subscriptionRepositoryMock, filters, mapper, followerEventPublisher);
    }


    @Test
    @DisplayName("Follow To Another User")
    void testFollowOneUserByAnotherUser() {
        subscriptionService.followUser(followerId, followeeId);
        Mockito.verify(subscriptionRepositoryMock, Mockito.times(1))
                .followUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Follow By Himself")
    void testFollowUserByHimself() {
        Mockito.when(subscriptionRepositoryMock.existsByFollowerIdAndFolloweeId(followerId, followerId))
                .thenThrow(new DataValidationException("!!"));
        Assert.assertThrows(DataValidationException.class,
                () -> subscriptionService.followUser(followerId, followerId));
    }

    @Test
    @DisplayName("Unfollow Another User")
    void testUnfollowOneUserFromAnotherUser() {
        subscriptionService.unfollowUser(followerId, followeeId);
        Mockito.verify(subscriptionRepositoryMock, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Get Followers Count")
    void testGetFollowersCount() {
        subscriptionService.getFollowersCount(followeeId);
        Mockito.verify(subscriptionRepositoryMock, Mockito.times(1))
                .findFollowersAmountByFolloweeId(followeeId);
    }

    @Test
    @DisplayName("Get Following Count")
    void testGetFollowingCount() {
        subscriptionService.getFollowingCount(followerId);
        Mockito.verify(subscriptionRepositoryMock, Mockito.times(1))
                .findFolloweesAmountByFollowerId(followerId);
    }

    @Test
    @DisplayName("Get Followers")
    void testGetFollowers() {
        SubscriptionUserFilterDto subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .page(1)
                .pageSize(10)
                .build();
        Mockito.when(subscriptionRepositoryMock.findByFolloweeId(followeeId)).thenReturn(allUsers.stream());
        List<SubscriptionUserDto> actualUsersDtos =
                subscriptionService.getFollowers(followeeId, subscriptionUserFilterDto);

        Assertions.assertEquals(3, actualUsersDtos.size());
    }

    @Test
    @DisplayName("Get Followees")
    void testGetFollowees() {
        SubscriptionUserFilterDto subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .page(1)
                .pageSize(10)
                .build();
        Mockito.when(subscriptionRepositoryMock.findByFolloweeId(followerId)).thenReturn(allUsers.stream());
        List<SubscriptionUserDto> actualUsersDtos =
                subscriptionService.getFollowing(followerId, subscriptionUserFilterDto);

        Assertions.assertEquals(3, actualUsersDtos.size());
    }

    @Test
    @DisplayName("Get Filtered Followees By Name")
    void testGetFilteredByNameFollowees() {
        SubscriptionUserFilterDto subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .namePattern("misha")
                .page(1)
                .pageSize(10)
                .build();

        Mockito.when(subscriptionRepositoryMock.findByFolloweeId(followerId)).thenReturn(allUsers.stream());
        List<SubscriptionUserDto> expectedUserDtos = allUsers.stream()
                .filter(u -> u.getId() == 1L)
                .map(user -> mapper.toSubscriptionUserDto(user))
                .toList();

        List<SubscriptionUserDto> actualUsersDtos =
                subscriptionService.getFollowing(followerId, subscriptionUserFilterDto);

        Assertions.assertEquals(expectedUserDtos, actualUsersDtos);
    }

    @Test
    @DisplayName("Get Filtered Followees By Experience")
    void testGetFilteredByExperienceFollowees() {
        SubscriptionUserFilterDto subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .experienceMin(20)
                .experienceMax(40)
                .page(1)
                .pageSize(10)
                .build();

        Mockito.when(subscriptionRepositoryMock.findByFolloweeId(followerId)).thenReturn(allUsers.stream());
        List<SubscriptionUserDto> expectedUserDtos = allUsers.stream()
                .filter(u -> (u.getId() == 2L || u.getId() == 3L))
                .map(user -> mapper.toSubscriptionUserDto(user))
                .toList();

        List<SubscriptionUserDto> actualUsersDtos =
                subscriptionService.getFollowing(followerId, subscriptionUserFilterDto);

        Assertions.assertEquals(expectedUserDtos, actualUsersDtos);
    }

    @Test
    @DisplayName("Get Filtered Followees By Experience on first page")
    void testGetFilteredByExperienceOnFirstPageFollowees() {
        SubscriptionUserFilterDto subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .experienceMin(20)
                .experienceMax(40)
                .page(1)
                .pageSize(1)
                .build();

        Mockito.when(subscriptionRepositoryMock.findByFolloweeId(followerId)).thenReturn(allUsers.stream());
        List<SubscriptionUserDto> expectedUserDtos = allUsers.stream()
                .filter(u -> u.getId() == 2L)
                .map(user -> mapper.toSubscriptionUserDto(user))
                .toList();

        List<SubscriptionUserDto> actualUsersDtos =
                subscriptionService.getFollowing(followerId, subscriptionUserFilterDto);

        Assertions.assertEquals(expectedUserDtos, actualUsersDtos);
    }

    @Test
    @DisplayName("Get Filtered Followees By Name and City")
    void testGetFilteredByNameCityFollowees() {
        SubscriptionUserFilterDto subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .namePattern("sha")
                .cityPattern("Kazan")
                .page(1)
                .pageSize(10)
                .build();

        Mockito.when(subscriptionRepositoryMock.findByFolloweeId(followerId)).thenReturn(allUsers.stream());
        List<SubscriptionUserDto> expectedUserDtos = allUsers.stream()
                .filter(u -> u.getId() == 3L)
                .map(user -> mapper.toSubscriptionUserDto(user))
                .toList();

        List<SubscriptionUserDto> actualUsersDtos =
                subscriptionService.getFollowing(followerId, subscriptionUserFilterDto);

        Assertions.assertEquals(expectedUserDtos, actualUsersDtos);
    }

    private List<SubscriptionFilter> generateFilters() {
        SubscriptionFilter aboutFilter = new SubscriptionUserAboutFilter();
        SubscriptionFilter cityFilter = new SubscriptionUserCityFilter();
        SubscriptionFilter contactFilter = new SubscriptionUserContactFilter();
        SubscriptionFilter countryFilter = new SubscriptionUserCountryFilter();
        SubscriptionFilter emailFilter = new SubscriptionUserEmailFilter();
        SubscriptionFilter experienceFilter = new SubscriptionUserExperienceFilter();
        SubscriptionFilter nameFilter = new SubscriptionUserNameFilter();
        SubscriptionFilter phoneFilter = new SubscriptionUserPhoneFilter();
        SubscriptionFilter skillFilter = new SubscriptionUserSkillFilter();

        List<SubscriptionFilter> filters = new ArrayList<>();
        filters.add(aboutFilter);
        filters.add(cityFilter);
        filters.add(contactFilter);
        filters.add(countryFilter);
        filters.add(emailFilter);
        filters.add(experienceFilter);
        filters.add(nameFilter);
        filters.add(phoneFilter);
        filters.add(skillFilter);

        return filters;
    }
}
