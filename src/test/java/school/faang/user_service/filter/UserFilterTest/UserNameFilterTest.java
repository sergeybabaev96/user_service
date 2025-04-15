package school.faang.user_service.filter.UserFilterTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserNameFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

public class UserNameFilterTest {
    private UserFilter filter;
    private UserFilterDto filterDto;

    private UserFilterDto createUserFilterDto(String namePattern) {
        return new UserFilterDto(
                namePattern,
                "",
                0,
                0
        );
    }

    private User createUser(String username) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setEmail("@mail.com");
        return user;
    }

    @BeforeEach
    public void setUp() {
        filter = new UserNameFilter();
        filterDto = createUserFilterDto("bob");
    }

    @Test
     void testIsApplicableWhenNameSet() {
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
     void testNoApplicableWhenNameIsNull() {
        filterDto = createUserFilterDto(null);
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void testNoApplicableWhenNameIsBlank() {
        filterDto = createUserFilterDto("");
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void testNoApplicableWhenNameIsEmpty() {
        filterDto = createUserFilterDto("  ");
        assertFalse(filter.isApplicable(filterDto));
    }


    @Test
     void testOneMatchApplyFilter() {
        User firstUser = createUser("Boby");
        User secondUser = createUser("Lola");
        applyAndAssertCount(Stream.of(firstUser, secondUser), filterDto, 1);
    }

    @Test
    void testMultipleMatchApplyFilter() {
        User firstUser = createUser("Boby");
        User secondUser = createUser("Bober");
        applyAndAssertCount(Stream.of(firstUser, secondUser), filterDto, 2);
    }

    @Test
    void testNoUsersMatchFilter() {
        User firstUser = createUser("Sarah");
        User secondUser = createUser("Lens");
        applyAndAssertCount(Stream.of(firstUser, secondUser), filterDto, 0);
    }

    @Test
    void testUserWithNullUserName() {
        User firstUser = createUser(null);
        User secondUser = createUser("Bob");
        applyAndAssertCount(Stream.of(firstUser, secondUser), filterDto, 1);
    }

    @Test
    void testUserWithBlankName() {
        User firstUser = createUser("");
        User secondUser = createUser("Bob");
        applyAndAssertCount(Stream.of(firstUser, secondUser), filterDto, 1);
    }

    @Test
    void testUserWithEmptyName() {
        User firstUser = createUser("  ");
        User secondUser = createUser("Bob");
        applyAndAssertCount(Stream.of(firstUser, secondUser), filterDto, 1);
    }

    private void applyAndAssertCount(Stream<User> users, UserFilterDto filterDto, long expectedCount) {
        long actualCount = filter.apply(users, filterDto).count();
        assertEquals(expectedCount, actualCount);
    }

}
