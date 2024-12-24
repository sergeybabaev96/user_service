package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController).build();
    }

    @Test
    void followUser_ShouldReturnCreated() throws Exception {
        long followerId = 1L;
        long followeeId = 2L;

        mockMvc.perform(post("/users/{followerId}/followees", followerId)
                        .param("followeeId", String.valueOf(followeeId)))
                .andExpect(status().isCreated());

        verify(subscriptionService, times(1)).followUser(followerId, followeeId);
    }

    @Test
    void unfollowUser_ShouldReturnNoContent() throws Exception {
        long followerId = 1L;
        long followeeId = 2L;

        mockMvc.perform(delete("/users/{followerId}/unfollow/{followeeId}", followerId, followeeId))
                .andExpect(status().isNoContent());

        verify(subscriptionService, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    void getFollowers_ShouldReturnOk() throws Exception {
        long followeeId = 1L;

        UserFilterDto filter = new UserFilterDto();
        filter.setName("Jane Doe");
        filter.setAbout("Graphic Designer");
        filter.setEmail("jane.doe@google.com");
        filter.setContact("0987654321");
        filter.setCountry("Canada");
        filter.setCity("Toronto");
        filter.setPhone("+1987654321");
        filter.setSkill("Design");
        filter.setExperienceMin(2);
        filter.setExperienceMax(6);
        filter.setPage(1);
        filter.setPageSize(10);

        List<UserDto> followers = Collections.singletonList(new UserDto());

        when(subscriptionService.getFollowers(followeeId, filter)).thenReturn(followers);

        mockMvc.perform(get("/users/{followeeId}/followers", followeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("name", filter.getName())
                        .param("about", filter.getAbout())
                        .param("email", filter.getEmail())
                        .param("contact", filter.getContact())
                        .param("country", filter.getCountry())
                        .param("city", filter.getCity())
                        .param("phone", filter.getPhone())
                        .param("skill", filter.getSkill())
                        .param("experienceMin", String.valueOf(filter.getExperienceMin()))
                        .param("experienceMax", String.valueOf(filter.getExperienceMax()))
                        .param("page", String.valueOf(filter.getPage()))
                        .param("pageSize", String.valueOf(filter.getPageSize())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(subscriptionService, times(1)).getFollowers(followeeId, filter);
    }

    @Test
    void getFollowersCount_ShouldReturnOk() throws Exception {
        long followerId = 1L;
        long count = 5L;

        when(subscriptionService.getFollowersCount(followerId)).thenReturn(count);

        mockMvc.perform(get("/users/{followerId}/followers/count", followerId))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(count)));

        verify(subscriptionService, times(1)).getFollowersCount(followerId);
    }

    @Test
    void getFollowing_ShouldReturnOk() throws Exception {
        long followeeId = 1L;

        UserFilterDto filter = new UserFilterDto();
        filter.setName("Alice Johnson");
        filter.setAbout("Digital Marketing Specialist with a passion for social media.");
        filter.setEmail("alice.johnson@yandex.ru");
        filter.setContact("9876543210");
        filter.setCountry("Russia");
        filter.setCity("Moskov");
        filter.setPhone("+14165551234");
        filter.setSkill("SEO, Content Marketing");
        filter.setExperienceMin(3);
        filter.setExperienceMax(10);
        filter.setPage(1);
        filter.setPageSize(20);

        List<UserDto> following = Collections.singletonList(new UserDto());

        when(subscriptionService.getFollowing(followeeId, filter)).thenReturn(following);

        mockMvc.perform(get("/users/{followeeId}/following", followeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("name", filter.getName())
                        .param("about", filter.getAbout())
                        .param("email", filter.getEmail())
                        .param("contact", filter.getContact())
                        .param("country", filter.getCountry())
                        .param("city", filter.getCity())
                        .param("phone", filter.getPhone())
                        .param("skill", filter.getSkill())
                        .param("experienceMin", String.valueOf(filter.getExperienceMin()))
                        .param("experienceMax", String.valueOf(filter.getExperienceMax()))
                        .param("page", String.valueOf(filter.getPage()))
                        .param("pageSize", String.valueOf(filter.getPageSize())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(subscriptionService, times(1)).getFollowing(followeeId, filter);
    }

    @Test
    void getFollowingCount_ShouldReturnOk() throws Exception {
        long followerId = 1L;
        long count = 3L;

        when(subscriptionService.getFollowingCount(followerId)).thenReturn(count);

        mockMvc.perform(get("/users/{followerId}/following/count", followerId))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(count)));

        verify(subscriptionService, times(1)).getFollowingCount(followerId);
    }
}
