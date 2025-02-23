package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.event.FollowEvent;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.util.BaseContextTest;
import school.faang.user_service.util.TestListener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SubscriptionControllerTest extends BaseContextTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${spring.data.redis.channel.follow}")
    private String followChannel;

    private final long followeeId = 1L;
    private final long followerId = 2L;

    @Test
    @Transactional
    void testFollow_SuccessCase() throws Exception {
        TestListener testListener = subscribeOnChannel(followChannel);

        mockMvc
                .perform(post("/subscriptions")
                        .param("followeeId", Long.toString(followeeId))
                        .header("x-user-id", followerId))
                .andExpect(status().isOk());

        assertTrue(existByFollowerIdAndFolloweeId(
                followerId, followeeId
        ));

        assertEquals(followChannel, testListener.getChannel());
        FollowEvent followEvent = objectMapper.readValue(
                testListener.getMessage(), FollowEvent.class
        );
        assertEquals(followerId, followEvent.getFollowerId());
        assertEquals(followeeId, followEvent.getFolloweeId());

    }

    @Test
    void testFollow_NotSpecifiedFolloweeId() throws Exception {
        mockMvc
                .perform(post("/subscriptions")
                        .header("x-user-id", followerId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFollow_NotSpecifiedFollowerId() throws Exception {
        mockMvc
                .perform(post("/subscriptions")
                        .param("followeeId", Long.toString(followeeId)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void testUnfollow_SuccessCase() throws Exception {
        followUser(followerId, followeeId);
        mockMvc
                .perform(delete("/subscriptions")
                        .param("followeeId", Long.toString(followeeId))
                        .header("x-user-id", followerId))
                .andExpect(status().isOk());
        assertFalse(existByFollowerIdAndFolloweeId(
                followerId, followeeId
        ));
    }

    @Test
    void testUnfollow_NotSpecifiedFolloweeId() throws Exception {
        mockMvc
                .perform(delete("/subscriptions")
                        .header("x-user-id", followerId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUnfollow_NotSpecifiedFollowerId() throws Exception {
        mockMvc
                .perform(delete("/subscriptions")
                        .param("followeeId", Long.toString(followeeId)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void testGetFollowers_SuccessCase() throws Exception {
        followUser(followerId, followeeId);
        followUser(3, followeeId);
        mockMvc
                .perform(get("/subscriptions/{followeeId}/followers", followeeId)
                        .param("namePattern", "Smith")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @Transactional
    void testGetFollowersCount_SuccessCase() throws Exception {
        followUser(followerId, followeeId);
        followUser(3, followeeId);
        mockMvc
                .perform(get("/subscriptions/{followeeId}/followers/count", followeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(2));
    }

    @Test
    @Transactional
    void testGetFollowing_SuccessCase() throws Exception {
        followUser(followerId, followeeId);
        followUser(followerId, 3);
        mockMvc
                .perform(get("/subscriptions/{followerId}/following", followerId)
                        .param("namePattern", "Doe")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @Transactional
    void testGetFollowingCount_SuccessCase() throws Exception {
        followUser(followerId, followeeId);
        followUser(followerId, 3);
        mockMvc
                .perform(get("/subscriptions/{followerId}/following/count", followerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(2));
    }


    private boolean existByFollowerIdAndFolloweeId(long followerId, long followeeId) {
        return subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    private void followUser(long followerId, long followeeId) {
        subscriptionRepository.followUser(followerId, followeeId);
    }
}
