package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class SubscriptionServiceTest {

    @MockBean
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .username("Alex")
                .phone("1234567890")
                .email("alex@gmail.com")
                .experience(2)
                .build();

        user2 = User.builder()
                .id(2L)
                .username("Sam")
                .phone("1234567890")
                .email("sam@mail.ru")
                .experience(5)
                .build();

        when(subscriptionRepository.findByFolloweeId(1L))
                .thenAnswer(inv -> Stream.of(user1, user2));
    }

    @Test
    @DisplayName("Фильтрация по имени: только Alex")
    void getFollowers_filtersByName() {
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setNamePattern("alex");

        List<UserDto> result = subscriptionService.getFollowers(1L, filterDto);

        assertEquals(1, result.size());
        assertEquals("Alex", result.get(0).getUsername());
        assertEquals(new UserDto(1L, "Alex", "alex@gmail.com"), result.get(0));
    }

    @Test
    @DisplayName("Фильтрация по телефону: оба проходят")
    void getFollowers_filtersByPhone_bothMatch() {
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setPhonePattern("1234567890");

        List<UserDto> result = subscriptionService.getFollowers(1L, filterDto);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Фильтрация по телефону: никто не проходит")
    void getFollowers_filtersByPhone_noneMatch() {
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setPhonePattern("234567890");

        List<UserDto> result = subscriptionService.getFollowers(1L, filterDto);

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Фильтрация по телефону и опыту: только Sam")
    void getFollowers_filtersByPhoneAndExperience() {
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setPhonePattern("1234567890");
        filterDto.setExperienceMin(3);
        filterDto.setExperienceMax(5);

        List<UserDto> result = subscriptionService.getFollowers(1L, filterDto);

        assertEquals(1, result.size());
        assertEquals("Sam", result.get(0).getUsername());
        assertEquals(new UserDto(2L, "Sam", "sam@mail.ru"), result.get(0));
    }
}