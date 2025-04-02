package school.faang.user_service.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.controller.subscription.SubscriptionController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.GlobalExceptionHandler;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SubscriptionController.class)
@ContextConfiguration(classes = {SubscriptionController.class, GlobalExceptionHandler.class})
public class SubscriptionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriptionService subscriptionService;

    private final long followerId = 1L;
    private final long followeeId = 2L;
    private final long invalidUserId = 999L;


    @Nested
    class FollowUser {
        @Test
        public void dataValidationException() throws Exception {
            doThrow(new DataValidationException("any text"))
                    .when(subscriptionService).followUser(invalidUserId, followeeId);

            mockMvc.perform(post("/subscriptions/{followerId}/follow/{followeeId}", invalidUserId, followeeId))
                    .andExpect(status().isBadRequest());
            verify(subscriptionService, times(1)).followUser(invalidUserId, followeeId);
        }

        @Test
        public void success() throws Exception {
            doNothing().when(subscriptionService).followUser(followerId, followeeId);

            mockMvc.perform(post("/subscriptions/{followerId}/follow/{followeeId}", followerId, followeeId))
                    .andExpect(status().isCreated());
            verify(subscriptionService, times(1)).followUser(followerId, followeeId);
        }
    }

    @Nested
    class UnfollowUser {
        @Test
        public void dataValidationException() throws Exception {
            doThrow(new DataValidationException("any text"))
                    .when(subscriptionService).unfollowUser(invalidUserId, followeeId);

            mockMvc.perform(delete("/subscriptions/{followerId}/unfollow/{followeeId}", invalidUserId, followeeId))
                    .andExpect(status().isBadRequest());
            verify(subscriptionService, times(1)).unfollowUser(invalidUserId, followeeId);
        }

        @Test
        public void success() throws Exception {
            doNothing().when(subscriptionService).unfollowUser(followerId, followeeId);

            mockMvc.perform(delete("/subscriptions/{followerId}/unfollow/{followeeId}", followerId, followeeId))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    class GetFollowers {
        @Test
        public void dataValidationException() throws Exception {
            UserFilterDto filterDto = new UserFilterDto();

            doThrow(new DataValidationException("any text"))
                    .when(subscriptionService).getFollowers(invalidUserId, filterDto);

            mockMvc.perform(get("/subscriptions/{followeeId}/followers/", invalidUserId))
                    .andExpect(status().isBadRequest());
            verify(subscriptionService, times(1)).getFollowers(invalidUserId, filterDto);
        }

        @Test
        public void shouldReturnFollowers() throws Exception {
            UserDto userDto = new UserDto();
            userDto.setUsername("Alice");

            when(subscriptionService.getFollowers(anyLong(), Mockito.any(UserFilterDto.class)))
                    .thenReturn(List.of(userDto));

            mockMvc.perform(get("/subscriptions/{followeeId}/followers/", followeeId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].username").value("Alice"));
        }
    }

    @Nested
    class GetFollowersCount {
        @Test
        public void dataValidationException() throws Exception {

            doThrow(new DataValidationException("any text"))
                    .when(subscriptionService).getFollowersCount(invalidUserId);

            mockMvc.perform(get("/subscriptions/{followeeId}/followers/count", invalidUserId))
                    .andExpect(status().isBadRequest());
            verify(subscriptionService, times(1)).getFollowersCount(invalidUserId);
        }

        @Test
        public void shouldReturnFollowersCount() throws Exception {
            when(subscriptionService.getFollowersCount(anyLong())).thenReturn(5);

            mockMvc.perform(get("/subscriptions/{followeeId}/followers/count", followeeId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("5"));
        }
    }

    @Nested
    class GetFollowing {
        @Test
        public void dataValidationException() throws Exception {
            UserFilterDto filterDto = new UserFilterDto();
            doThrow(new DataValidationException("any text"))
                    .when(subscriptionService).getFollowing(invalidUserId,filterDto);

            mockMvc.perform(get("/subscriptions/{followerId}/following", invalidUserId))
                    .andExpect(status().isBadRequest());
            verify(subscriptionService, times(1)).getFollowing(invalidUserId,filterDto);
        }

        @Test
        public void shouldReturnFollowing() throws Exception {
            UserDto userDto = new UserDto();
            userDto.setUsername("Bob");

            when(subscriptionService.getFollowing(anyLong(), Mockito.any(UserFilterDto.class)))
                    .thenReturn(List.of(userDto));

            mockMvc.perform(get("/subscriptions/{followerId}/following", followerId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].username").value("Bob"));
        }
    }

    @Nested
    class GetFollowingCount {
        @Test
        public void dataValidationException() throws Exception {
            doThrow(new DataValidationException("any text"))
                    .when(subscriptionService).getFollowingCount(invalidUserId);

            mockMvc.perform(get("/subscriptions/{followerId}/following/count", invalidUserId))
                    .andExpect(status().isBadRequest());
            verify(subscriptionService, times(1)).getFollowingCount(invalidUserId);
        }

        @Test
        public void shouldReturnFollowingCount() throws Exception {
            when(subscriptionService.getFollowingCount(anyLong())).thenReturn(3);

            mockMvc.perform(get("/subscriptions/{followerId}/following/count", followerId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("3"));
        }
    }

}
