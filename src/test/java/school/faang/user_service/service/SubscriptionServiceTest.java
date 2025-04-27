package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import school.faang.user_service.dto.publisher.FollowerEventDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование сервиса управления подписками")
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private FollowerEventPublisher followerEventPublisher;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Nested
    @DisplayName("Метод followUser")
    class FollowUserTest {

        @Test
        @DisplayName("Попытка подписки на самого себя выбрасывает исключение")
        void givenSameIds_WhenFollowUser_ThenThrowDataValidationException() {
            assertThatThrownBy(() -> subscriptionService.followUser(1L, 1L))
                    .isInstanceOf(DataValidationException.class)
                    .hasMessage("Cannot follow yourself");

            verifyNoInteractions(subscriptionRepository);
        }

        @Test
        @DisplayName("Подписка, которая уже существует, вызывает исключение")
        void givenExistingSubscription_WhenFollowUser_ThenThrowDataValidationException() {
            doThrow(DataIntegrityViolationException.class)
                    .when(subscriptionRepository).followUser(1L, 2L);

            assertThatThrownBy(() -> subscriptionService.followUser(1L, 2L))
                    .isInstanceOf(DataValidationException.class)
                    .hasMessage("Subscription already exists");
        }

        @Test
        @DisplayName("Успешная подписка пользователя")
        void givenValidIds_WhenFollowUser_ThenSubscriptionCreated() {
            long followerId = 1L;
            long followeeId = 2L;

            subscriptionService.followUser(followerId, followeeId);

            verify(subscriptionRepository).followUser(followerId, followeeId);

            ArgumentCaptor<FollowerEventDto> eventCaptor = ArgumentCaptor.forClass(FollowerEventDto.class);
            verify(followerEventPublisher).publish(eventCaptor.capture());

            FollowerEventDto publishedEvent = eventCaptor.getValue();
            assertEquals(followerId, publishedEvent.getFollowerId());
            assertEquals(followeeId, publishedEvent.getFolloweeId());
            assertNotNull(publishedEvent.getTimestamp());
        }
    }

    @Nested
    @DisplayName("Метод unfollowUser")
    class UnfollowUserTest {

        @Test
        @DisplayName("Попытка отписки от самого себя выбрасывает исключение")
        void givenSameIds_WhenUnfollowUser_ThenThrowDataValidationException() {
            assertThatThrownBy(() -> subscriptionService.unfollowUser(1L, 1L))
                    .isInstanceOf(DataValidationException.class)
                    .hasMessage("Cannot unfollow yourself");

            verifyNoInteractions(subscriptionRepository);
        }

        @Test
        @DisplayName("Успешная отписка пользователя")
        void givenValidIds_WhenUnfollowUser_ThenSubscriptionRemoved() {
            subscriptionService.unfollowUser(1L, 2L);

            verify(subscriptionRepository).unfollowUser(1L, 2L);
        }
    }

    @Nested
    @DisplayName("Метод getFollowers")
    class GetFollowersTest {

        @Test
        @DisplayName("Получение списка подписчиков с фильтрацией")
        void givenUserIdAndFilter_WhenGetFollowers_ThenReturnFilteredList() {
            User user = new User();
            UserViewDto dto = new UserViewDto();
            dto.setUsername("testuser");
            dto.setPhone("123");
            dto.setExperience(3);

            when(subscriptionRepository.findByFolloweeId(1L)).thenReturn(Stream.of(user));
            when(userMapper.toViewDto(user)).thenReturn(dto);

            UserFilterDto filter = new UserFilterDto();
            filter.setNamePattern("test");
            filter.setPhonePattern("123");
            filter.setExperienceMin(1);
            filter.setExperienceMax(5);

            List<UserViewDto> result = subscriptionService.getFollowers(1L, filter);

            assertThat(result).containsExactly(dto);
        }
    }

    @Nested
    @DisplayName("Метод getFollowing")
    class GetFollowingTest {

        @Test
        @DisplayName("Получение списка подписок с фильтрацией")
        void givenUserIdAndFilter_WhenGetFollowing_ThenReturnFilteredList() {
            User user = new User();
            UserViewDto dto = new UserViewDto();
            dto.setUsername("john");
            dto.setPhone("456");
            dto.setExperience(2);

            when(subscriptionRepository.findByFollowerId(1L)).thenReturn(Stream.of(user));
            when(userMapper.toViewDto(user)).thenReturn(dto);

            UserFilterDto filter = new UserFilterDto();
            filter.setNamePattern("john");
            filter.setPhonePattern("456");
            filter.setExperienceMin(1);
            filter.setExperienceMax(3);

            List<UserViewDto> result = subscriptionService.getFollowing(1L, filter);

            assertThat(result).containsExactly(dto);
        }
    }

    @Nested
    @DisplayName("Методы подсчёта количества")
    class CountMethodsTest {

        @Test
        @DisplayName("Получение количества подписчиков")
        void givenFolloweeId_WhenGetFollowersCount_ThenReturnCount() {
            when(subscriptionRepository.findFollowersAmountByFolloweeId(1L)).thenReturn(5);

            int count = subscriptionService.getFollowersCount(1L);

            assertThat(count).isEqualTo(5);
        }

        @Test
        @DisplayName("Получение количества подписок")
        void givenFollowerId_WhenGetFollowingCount_ThenReturnCount() {
            when(subscriptionRepository.findFolloweesAmountByFollowerId(1L)).thenReturn(3);

            int count = subscriptionService.getFollowingCount(1L);

            assertThat(count).isEqualTo(3);
        }
    }
}