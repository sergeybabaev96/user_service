package school.faang.user_service.filter.subscriber;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubscriberNameFilterTest {

    private SubscriberNameFilter subscriberNameFilter = new SubscriberNameFilter();
    UserFilterDto filterDto;
    private  MockUsers mockUsers = new MockUsers();

    @BeforeEach
    void setUp() {
        filterDto = new UserFilterDto();
    }

    @Test
    void isApplicableNameNull(){
        filterDto.setNamePattern(null);

        assertFalse(subscriberNameFilter.isApplicable(filterDto));
    }

    @Test
    void isApplicableNameEmpty(){
        filterDto.setNamePattern("");

        assertFalse(subscriberNameFilter.isApplicable(filterDto));
    }

    @Test
    void isApplicableNameCorrect(){
        filterDto.setNamePattern(mockUsers.user1.getUsername());

        assertTrue(subscriberNameFilter.isApplicable(filterDto));
    }

    @Test
    void applySuccess() {
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setNamePattern(mockUsers.user1.getUsername());

        List<User> filteredUsers = subscriberNameFilter.apply(mockUsers.getUsers(), filterDto).toList();
        assertEquals(1, filteredUsers.size());
        assertEquals(mockUsers.user1.getUsername(), filteredUsers.get(0).getUsername());
    }
}