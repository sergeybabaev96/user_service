package school.faang.user_service.service.subscription.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.filter.PhoneFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class PhoneFilterTest {
    private PhoneFilter phoneFilter;
    private UserFilterDto userFilterDto;
    private Stream<User> users;
    private User firstUser;
    private User secondUser;
    private User thirdUser;

    @BeforeEach
    void setup() {
        phoneFilter = new PhoneFilter();

        userFilterDto = new UserFilterDto();
        userFilterDto.setPhonePattern("123");

        firstUser = new User();
        firstUser.setPhone("1234567");

        secondUser = new User();
        secondUser.setPhone("98765432");

        thirdUser = new User();
        thirdUser.setPhone("111111111");

        users = Stream.of(firstUser, secondUser, thirdUser);
    }

    @Test
    void shouldBeApplicableWhenPhonePatternIsNotNull() {
        boolean isApplicable = phoneFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void shouldNotBeApplicableWhenPhonePatternIsNull() {
        userFilterDto.setPhonePattern(null);
        boolean isApplicable = phoneFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void shouldFilterUsersByPhonePattern() {
        userFilterDto.setPhonePattern("123");
        List<User> filteredUsers = phoneFilter.apply(users, userFilterDto);

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(firstUser));
    }
}
