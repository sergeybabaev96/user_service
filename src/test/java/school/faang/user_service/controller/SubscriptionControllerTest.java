package school.faang.user_service.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.controller.subscription.SubscriptionController;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты контроллера подписок")
class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Nested
    @DisplayName("Тесты подписки на пользователя")
    class FollowUserTests {
        @Test
        @DisplayName("Успешная подписка - должен вернуть статус 201 Created")
        void givenValidFollowerAndFolloweeIds_whenFollowUser_thenReturnCreatedStatus() {
            long followerId = 1L;
            long followeeId = 2L;

            ResponseEntity<Void> response = subscriptionController.followUser(followerId, followeeId);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            verify(subscriptionService).followUser(followerId, followeeId);
        }
    }

    @Nested
    @DisplayName("Тесты отписки от пользователя")
    class UnfollowUserTests {
        @Test
        @DisplayName("Успешная отписка - должен вернуть статус 204 No Content")
        void givenValidFollowerAndFolloweeIds_whenUnfollowUser_thenReturnNoContentStatus() {
            long followerId = 1L;
            long followeeId = 2L;

            ResponseEntity<Void> response = subscriptionController.unfollowUser(followerId, followeeId);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            verify(subscriptionService).unfollowUser(followerId, followeeId);
        }
    }

    @Nested
    @DisplayName("Тесты получения подписчиков")
    class GetFollowersTests {
        @Test
        @DisplayName("Получение списка подписчиков - должен вернуть список пользователей")
        void givenUserIdAndFilter_whenGetFollowers_thenReturnListOfUsers() {
            long userId = 1L;
            UserFilterDto filter = new UserFilterDto();
            List<UserViewDto> expectedUsers = List.of(new UserViewDto(), new UserViewDto());

            when(subscriptionService.getFollowers(userId, filter)).thenReturn(expectedUsers);

            List<UserViewDto> result = subscriptionController.getFollowers(userId, filter);

            assertEquals(expectedUsers, result);
            verify(subscriptionService).getFollowers(userId, filter);
        }

        @Test
        @DisplayName("Получение количества подписчиков - должен вернуть число")
        void givenUserId_whenGetFollowersCount_thenReturnCount() {
            long userId = 1L;
            int expectedCount = 5;

            when(subscriptionService.getFollowersCount(userId)).thenReturn(expectedCount);

            int result = subscriptionController.getFollowersCount(userId);

            assertEquals(expectedCount, result);
            verify(subscriptionService).getFollowersCount(userId);
        }
    }

    @Nested
    @DisplayName("Тесты получения подписок")
    class GetFollowingTests {
        @Test
        @DisplayName("Получение списка подписок - должен вернуть список пользователей")
        void givenUserIdAndFilter_whenGetFollowing_thenReturnListOfUsers() {
            long userId = 1L;
            UserFilterDto filter = new UserFilterDto();
            List<UserViewDto> expectedUsers = List.of(new UserViewDto(), new UserViewDto());

            when(subscriptionService.getFollowing(userId, filter)).thenReturn(expectedUsers);

            List<UserViewDto> result = subscriptionController.getFollowing(userId, filter);

            assertEquals(expectedUsers, result);
            verify(subscriptionService).getFollowing(userId, filter);
        }

        @Test
        @DisplayName("Получение количества подписок - должен вернуть число")
        void givenUserId_whenGetFollowingCount_thenReturnCount() {
            long userId = 1L;
            int expectedCount = 3;

            when(subscriptionService.getFollowingCount(userId)).thenReturn(expectedCount);

            int result = subscriptionController.getFollowingCount(userId);

            assertEquals(expectedCount, result);
            verify(subscriptionService).getFollowingCount(userId);
        }
    }
}