package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserAboutFilter;
import school.faang.user_service.filter.user.UserFilter;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class UserAboutFilterTest {
    UserFilter userFilter = new UserAboutFilter();

    @Test
    void isApplicable() {
        UserFilterDto userFilterDto = UserFilterDto.builder()
                .aboutPattern("About user")
                .build();

        boolean actual = userFilter.isApplicable(userFilterDto);

        Assertions.assertTrue(actual);
    }

    @Test
    void isApplicableAboutFilterIsNull() {
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
        Stream<User> users = IntStream.range(0, 10)
                .boxed()
                .map(i -> User.builder()
                        .aboutMe("About user%d".formatted(i))
                        .build());

        UserFilterDto userFilterDto = UserFilterDto.builder().aboutPattern("user5").build();
        List<User> except = List.of(User.builder().aboutMe("About user5").build());
        List<User> actual = userFilter.apply(users, userFilterDto)
                .toList();

        Assertions.assertEquals(except, actual);
    }

    @Test
    void applyAboutFilterIsNull() {
        Stream<User> users = IntStream.range(0, 10)
                .boxed()
                .map(i -> User.builder()
                        .aboutMe("About user%d".formatted(i))
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
                        .aboutMe("About user%d".formatted(i))
                        .build());

        List<User> except = List.of();
        List<User> actual = userFilter.apply(users, null)
                .toList();

        Assertions.assertEquals(except, actual);
    }
}