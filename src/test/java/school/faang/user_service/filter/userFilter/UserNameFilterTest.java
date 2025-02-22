package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserNameFilter;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class UserNameFilterTest {
    UserFilter userFilter = new UserNameFilter();

    @Test
    void isApplicable() {
        UserFilterDto userFilterDto = UserFilterDto.builder()
                .namePattern("user1")
                .build();

        boolean actual = userFilter.isApplicable(userFilterDto);

        Assertions.assertTrue(actual);
    }

    @Test
    void isApplicableNameFilterIsNull() {
        UserFilterDto userFilterDto = new UserFilterDto();
        boolean actual = userFilter.isApplicable(userFilterDto);

        Assertions.assertFalse(actual);
    }

    @Test
    void isApplicableUserFilterDtoIsNull() {
        boolean actual = userFilter.isApplicable(null);

        Assertions.assertFalse(actual);
    }

    @Test
    void apply() {
        Stream<User> users = IntStream.range(0, 11)
                .boxed()
                .map(i -> User.builder()
                        .username("User%d".formatted(i))
                        .build());

        UserFilterDto userFilterDto = UserFilterDto.builder().namePattern("user1").build();
        List<User> except = List.of(
                User.builder().username("User1").build(),
                User.builder().username("User10").build()
        );
        List<User> actual = userFilter.apply(users, userFilterDto)
                .toList();

        Assertions.assertEquals(except, actual);
    }

    @Test
    void applyNameFilterIsNull() {
        Stream<User> users = IntStream.range(0, 10)
                .boxed()
                .map(i -> User.builder()
                        .username("User%d".formatted(i))
                        .build());

        UserFilterDto userFilterDto = UserFilterDto.builder().build();
        List<User> except = List.of();
        List<User> actual = userFilter.apply(users, userFilterDto)
                .toList();

        Assertions.assertEquals(except, actual);
    }

    @Test
    void applyFilterDtoIsNull() {
        Stream<User> users = IntStream.range(0, 10)
                .boxed()
                .map(i -> User.builder()
                        .username("User%d".formatted(i))
                        .build());

        List<User> except = List.of();
        List<User> actual = userFilter.apply(users, null)
                .toList();

        Assertions.assertEquals(except, actual);
    }
}