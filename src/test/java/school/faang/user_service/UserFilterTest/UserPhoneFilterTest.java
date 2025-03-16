package school.faang.user_service.UserFilterTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserPhoneFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserPhoneFilterTest {
    private UserFilter filter;
    private UserFilterDto filterDto;

    private UserFilterDto createUserFilterDto(String phonePattern) {
        return new UserFilterDto(
                "",
                phonePattern,
                0,
                0
        );
    }

    private User createUser(String phone) {
        User user = new User();
        user.setPhone(phone);
        return user;
    }

    @BeforeEach
    public void setUp() {
        filter = new UserPhoneFilter();
        filterDto = createUserFilterDto("123");
    }

    @Test
    void testIsApplicableWhenPhoneSet() {
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void testNoApplicableWhenPhoneIsNull() {
        filterDto = createUserFilterDto(null);
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void testNoApplicableWhenPhoneIsBlank() {
        filterDto = createUserFilterDto("");
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void testNoApplicableWhenPhoneIsEmpty() {
        filterDto = createUserFilterDto("  ");
        assertFalse(filter.isApplicable(filterDto));
    }


    @Test
    void testOneMatchingApplyFilter() {
        User firstUser = createUser("+79123347588");
        User secondUser = createUser("+79928389981");
        applyAndAssertCount(Stream.of(firstUser, secondUser), filterDto, 1);
    }

    @Test
    void testMultipleMatchingApplyFilter() {
        User firstUser = createUser("+79123347588");
        User secondUser = createUser("+79928381231");
        applyAndAssertCount(Stream.of(firstUser, secondUser), filterDto, 2);
    }

    @Test
    void testNoMatchingApplyFilter() {
        User firstUser = createUser("+79998887766");
        User secondUser = createUser("+799977776655");
        applyAndAssertCount(Stream.of(firstUser, secondUser), filterDto, 0);
    }

    @Test
    void testUserWithNullPhone() {
        User firstUser = createUser(null);
        User secondUser = createUser("+791237789231");
        applyAndAssertCount(Stream.of(firstUser, secondUser), filterDto, 1);
    }

    @Test
    void testUserWithEmptyPhone() {
        User firstUser = createUser("");
        User secondUser = createUser("+791237789231");
        applyAndAssertCount(Stream.of(firstUser, secondUser), filterDto, 1);
    }

    @Test
    void testUserWithBlankPhone() {
        User firstUser = createUser("  ");
        User secondUser = createUser("+791237789231");
        applyAndAssertCount(Stream.of(firstUser, secondUser), filterDto, 1);
    }

    private void applyAndAssertCount(Stream<User> users, UserFilterDto filterDto, long expectedCount) {
        long actualCount = filter.apply(users, filterDto).count();
        assertEquals(expectedCount, actualCount);
    }

}
