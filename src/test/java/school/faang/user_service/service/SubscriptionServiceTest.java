package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserAboutFilter;
import school.faang.user_service.filter.user.UserCityFilter;
import school.faang.user_service.filter.user.UserContactFilter;
import school.faang.user_service.filter.user.UserCountryFilter;
import school.faang.user_service.filter.user.UserEmailFilter;
import school.faang.user_service.filter.user.UserExperienceMaxFilter;
import school.faang.user_service.filter.user.UserExperienceMinFilter;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserNameFilter;
import school.faang.user_service.filter.user.UserPageFilter;
import school.faang.user_service.filter.user.UserPhoneFilter;
import school.faang.user_service.filter.user.UserSkillFilter;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    SubscriptionRepository subscriptionRepository = Mockito.mock(SubscriptionRepository.class);
    List<UserFilter> userFilters = new ArrayList<>();
    UserFilter aboutFilter = Mockito.mock(UserAboutFilter.class);
    UserFilter cityFilter = Mockito.mock(UserCityFilter.class);
    UserFilter contactFilter = Mockito.mock(UserContactFilter.class);
    UserFilter countryFilter = Mockito.mock(UserCountryFilter.class);
    UserFilter emailFilter = Mockito.mock(UserEmailFilter.class);
    UserFilter experienceMaxFilter = Mockito.mock(UserExperienceMaxFilter.class);
    UserFilter experienceMinFilter = Mockito.mock(UserExperienceMinFilter.class);
    UserFilter nameFilter = Mockito.mock(UserNameFilter.class);
    UserFilter pageFilter = Mockito.mock(UserPageFilter.class);
    UserFilter skillFilter = Mockito.mock(UserSkillFilter.class);
    UserFilter phoneFilter = Mockito.mock(UserPhoneFilter.class);
    SubscriptionService subscriptionService = new SubscriptionService(subscriptionRepository, userFilters);

    @BeforeEach
    void mockUserFilters() {
        userFilters.add(aboutFilter);
        userFilters.add(cityFilter);
        userFilters.add(contactFilter);
        userFilters.add(countryFilter);
        userFilters.add(emailFilter);
        userFilters.add(experienceMaxFilter);
        userFilters.add(experienceMinFilter);
        userFilters.add(nameFilter);
        userFilters.add(phoneFilter);
        userFilters.add(skillFilter);
        userFilters.add(pageFilter);
    }

    @Test
    void followUser() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(4, 5))
                .thenReturn(false);
        Assertions.assertDoesNotThrow(() -> subscriptionService.followUser(4, 5));
        Mockito.verify(subscriptionRepository).existsByFollowerIdAndFolloweeId(4, 5);
    }

    @Test
    void followSameUser() {
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(4, 4),
                "FollowerId 4 and FolloweeId 4 cannot be the same");
        Mockito.verify(subscriptionRepository, Mockito.never()).existsByFollowerIdAndFolloweeId(4, 4);
    }

    @Test
    void followIsExist() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(4, 5))
                .thenReturn(true);
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(4, 5),
                "This subscription (4 - 5) already exists");
        Mockito.verify(subscriptionRepository).existsByFollowerIdAndFolloweeId(4, 5);
    }

    @Test
    void unfollowUser() {
        Assertions.assertDoesNotThrow(() -> subscriptionService.unfollowUser(4, 5));
        Mockito.verify(subscriptionRepository).unfollowUser(4, 5);
    }

    @Test
    void getFollowers() {
        Stream<User> mockedUsers = getMockedUsers(100);
        Mockito.when(subscriptionRepository.findByFollowerId(Mockito.anyLong()))
                .thenReturn(mockedUsers);

        UserFilterDto filters = getFilter();
        Stream<User> getMockedStream = getMockedUsers(20);

        Mockito.when(aboutFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(cityFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(contactFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(countryFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(emailFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(experienceMaxFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(experienceMinFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(nameFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(pageFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(phoneFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(skillFilter.isApplicable(filters)).thenReturn(true);

        Mockito.when(aboutFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(cityFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(contactFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(countryFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(emailFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(experienceMaxFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(experienceMinFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(nameFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(skillFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(phoneFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(pageFilter.apply(mockedUsers, filters)).thenReturn(getMockedStream);

        List<User> users = subscriptionService.getFollowers(3L, filters);
        List<User> expectedUsers = getMockedUsers(20).toList();

        Assertions.assertTrue(expectedUsers.containsAll(users));
        Assertions.assertTrue(users.containsAll(expectedUsers));
    }

    @Test
    void getFollowersCount() {
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(Mockito.anyLong()))
                .thenReturn(77);

        int expectedCount = 77;
        int actualCount = subscriptionService.getFollowingCount(3L);

        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void getFollowersCountNegative() {
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(Mockito.anyLong()))
                .thenReturn(0);
        int expectedCount = 0;
        int actualCount = subscriptionService.getFollowingCount(3L);
        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void getFollowing() {
        Stream<User> mockedUsers = getMockedUsers(100);
        Mockito.when(subscriptionRepository.findByFolloweeId(Mockito.anyLong()))
                .thenReturn(mockedUsers);

        List<User> expectedUsers = getMockedUsers(20).toList();
        Stream<User> getMockedStream = getMockedUsers(20);
        UserFilterDto filters = getFilter();

        Mockito.when(aboutFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(cityFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(contactFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(countryFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(emailFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(experienceMaxFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(experienceMinFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(nameFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(pageFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(phoneFilter.isApplicable(filters)).thenReturn(true);
        Mockito.when(skillFilter.isApplicable(filters)).thenReturn(true);

        Mockito.when(aboutFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(cityFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(contactFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(countryFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(emailFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(experienceMaxFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(experienceMinFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(nameFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(skillFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(phoneFilter.apply(mockedUsers, filters)).thenReturn(mockedUsers);
        Mockito.when(pageFilter.apply(mockedUsers, filters)).thenReturn(getMockedStream);

        List<User> users = subscriptionService.getFollowing(3L, filters);
        Assertions.assertEquals(expectedUsers, users);
    }

    @Test
    void getFollowingCount() {
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(Mockito.anyLong()))
                .thenReturn(77);
        int expectedCount = 77;
        int actualCount = subscriptionService.getFollowingCount(3L);
        Assertions.assertEquals(expectedCount, actualCount);
        Mockito.verify(subscriptionRepository).findFolloweesAmountByFollowerId(3L);
    }

    @Test
    void getFollowingCountNegative() {
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(Mockito.anyLong()))
                .thenReturn(0);
        int expectedCount = 0;
        int actualCount = subscriptionService.getFollowingCount(3L);
        Assertions.assertEquals(expectedCount, actualCount);
        Mockito.verify(subscriptionRepository).findFolloweesAmountByFollowerId(3L);
    }

    private Stream<User> getMockedUsers(int count) {
        return IntStream.range(0, count)
                .boxed()
                .map(i -> User.builder()
                        .id(i.longValue())
                        .username("user%d".formatted(i))
                        .email("user%d@email.com".formatted(i))
                        .aboutMe("About user%d".formatted(i))
                        .contacts(List.of(
                                new Contact(0, new User(), "Contact%d".formatted(i), ContactType.CUSTOM),
                                new Contact(1, new User(), "@user%d".formatted(i), ContactType.TELEGRAM)
                        ))
                        .country(new Country(1, "Russia", List.of()))
                        .city("Moscow")
                        .phone("+14560245628")
                        .skills(List.of(
                                new Skill(0, "Skill1", List.of(), List.of(), List.of(), List.of(), null, null)
                        ))
                        .experience(5)
                        .build());

    }

    private UserFilterDto getFilter() {
        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern("user");
        filter.setAboutPattern("about");
        filter.setEmailPattern(".com");
        filter.setContactPattern("contact");
        filter.setCountryPattern("Russia");
        filter.setCityPattern("Moscow");
        filter.setPhonePattern("+14");
        filter.setSkillPattern("skill");
        filter.setExperienceMin(1);
        filter.setExperienceMax(10);
        filter.setPage(2);
        filter.setPageSize(10);

        return filter;
    }
}