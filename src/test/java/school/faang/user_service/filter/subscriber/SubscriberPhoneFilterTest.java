package school.faang.user_service.filter.subscriber;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SubscriberPhoneFilterTest {

    private SubscriberPhoneFilter subscriberPhoneFilter = new SubscriberPhoneFilter();
    UserFilterDto filterDto;
    private  MockUsers mockUsers = new MockUsers();

    @BeforeEach
    void setUp() {
        filterDto = new UserFilterDto();
    }

    @Test
    void isApplicableNameNull(){
        filterDto.setPhonePattern(null);

        assertFalse(subscriberPhoneFilter.isApplicable(filterDto));
    }

    @Test
    void isApplicableNameEmpty(){
        filterDto.setPhonePattern("");

        assertFalse(subscriberPhoneFilter.isApplicable(filterDto));
    }

    @Test
    void isApplicableNameCorrect(){
        filterDto.setPhonePattern(mockUsers.user1.getPhone());

        assertTrue(subscriberPhoneFilter.isApplicable(filterDto));
    }

    @Test
    void applyNoMatch() {
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setPhonePattern(mockUsers.user1.getPhone());
        User noMatchUser = new User();
        noMatchUser.setPhone("34244234");

        List<User> filteredUsers = subscriberPhoneFilter.apply(Stream.of(noMatchUser), filterDto).toList();
        assertEquals(0, filteredUsers.size());
    }

    @Test
    void applySuccess() {
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setPhonePattern(mockUsers.user1.getPhone());

        List<User> filteredUsers = subscriberPhoneFilter.apply(mockUsers.getUsers(), filterDto).toList();
        assertEquals(1, filteredUsers.size());
        assertEquals(mockUsers.user1.getPhone(), filteredUsers.get(0).getPhone());
    }
}