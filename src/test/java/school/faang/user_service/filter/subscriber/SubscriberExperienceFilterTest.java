package school.faang.user_service.filter.subscriber;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubscriberExperienceFilterTest {

    private SubscriberExperienceFilter subscriberExperienceFilter = new SubscriberExperienceFilter();
    private UserFilterDto filterDto;
    private  MockUsers mockUsers = new MockUsers();

    @BeforeEach
    void setUp() {
        filterDto = new UserFilterDto();
    }

    @Test
    void experienceMinMoreZero() {
        filterDto.setExperienceMin(1);

        assertTrue(subscriberExperienceFilter.isApplicable(filterDto));
    }

    @Test
    void experienceMaxMoreZero() {
        filterDto.setExperienceMax(1);

        assertTrue(subscriberExperienceFilter.isApplicable(filterDto));
    }

    @Test
    void experienceMinLessZero() {
        filterDto.setExperienceMin(-1);

        assertFalse(subscriberExperienceFilter.isApplicable(filterDto));
    }

    @Test
    void experienceMaxLessZero() {
        filterDto.setExperienceMax(-1);

        assertFalse(subscriberExperienceFilter.isApplicable(filterDto));
    }

    @Test
    void applySuccess() {
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setExperienceMin(mockUsers.user1.getExperience());
        filterDto.setExperienceMax(mockUsers.user1.getExperience()+1);

        List<User> filteredUsers = subscriberExperienceFilter.apply(mockUsers.getUsers(), filterDto).toList();
        assertEquals(1, filteredUsers.size());
        assertEquals(mockUsers.user1.getExperience(), filteredUsers.get(0).getExperience());
    }
}