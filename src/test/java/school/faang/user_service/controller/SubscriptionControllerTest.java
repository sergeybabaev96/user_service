package school.faang.user_service.controller;

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
import school.faang.user_service.service.subscription.SubscriptionService;


import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SubscriptionController.class)
@ContextConfiguration(classes = SubscriptionController.class)
public class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriptionService subscriptionService;

    private final long followerId = 1L;
    private final long followeeId = 2L;

    @Test
    public void shouldFollowUser() throws Exception {
        doNothing().when(subscriptionService).followUser(followerId, followeeId);

        mockMvc.perform(post("/subscriptions/{followerId}/follow/{followeeId}", followerId, followeeId))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldUnfollowUser() throws Exception {
        doNothing().when(subscriptionService).unfollowUser(followerId, followeeId);

        mockMvc.perform(delete("/subscriptions/{followerId}/unfollow/{followeeId}", followerId, followeeId))
                .andExpect(status().isNoContent());
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

    @Test
    public void shouldReturnFollowersCount() throws Exception {
        when(subscriptionService.getFollowersCount(anyLong())).thenReturn(5);

        mockMvc.perform(get("/subscriptions/{followeeId}/followers/count", followeeId))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
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

    @Test
    public void shouldReturnFollowingCount() throws Exception {
        when(subscriptionService.getFollowingCount(anyLong())).thenReturn(3);

        mockMvc.perform(get("/subscriptions/{followerId}/following/count", followerId))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }
}
