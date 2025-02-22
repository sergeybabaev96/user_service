package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserPageFilter;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class UserPageFilterTest {
    UserFilter userFilter = new UserPageFilter();

    @Test
    void isApplicable() {
        UserFilterDto userFilterDto = UserFilterDto.builder()
                .page(2)
                .pageSize(10)
                .build();

        boolean actual = userFilter.isApplicable(userFilterDto);

        Assertions.assertTrue(actual);
    }

    @Test
    void isApplicablePageFilterIsNull() {
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
        Stream<User> users = IntStream.range(0, 20)
                .boxed()
                .map(i -> User.builder()
                        .build());

        UserFilterDto userFilterDto = UserFilterDto.builder()
                .page(3)
                .pageSize(4)
                .build();

        long except = 12;
        long actual = userFilter.apply(users, userFilterDto)
                .count();

        Assertions.assertEquals(except, actual);
    }

    @Test
    void applyPageFilterIsNull() {
        Stream<User> users = IntStream.range(0, 10)
                .boxed()
                .map(i -> User.builder()
                        .build());

        UserFilterDto userFilterDto = UserFilterDto.builder().build();
        List<User> actual = userFilter.apply(users, userFilterDto)
                .toList();

        Assertions.assertTrue(actual.isEmpty());
    }

    @Test
    void applyFilterDtoIsNull() {
        Stream<User> users = IntStream.range(0, 10)
                .boxed()
                .map(i -> User.builder()
                        .build());

        List<User> actual = userFilter.apply(users, null)
                .toList();

        Assertions.assertTrue(actual.isEmpty());
    }
}