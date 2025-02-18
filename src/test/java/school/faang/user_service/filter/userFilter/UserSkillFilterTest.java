package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserSkillFilter;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class UserSkillFilterTest {
    UserFilter userFilter = new UserSkillFilter();

    @Test
    void isApplicable() {
        UserFilterDto userFilterDto = UserFilterDto.builder()
                .skillPattern("skill1")
                .build();

        boolean actual = userFilter.isApplicable(userFilterDto);

        Assertions.assertTrue(actual);
    }

    @Test
    void isApplicableSkillFilterIsNull() {
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
                        .skills(IntStream.range(0, 5)
                                .boxed()
                                .map(j -> Skill.builder().title("Skill%d%d".formatted(i, j)).build())
                                .toList()
                        )
                        .build()
                );

        UserFilterDto userFilterDto = UserFilterDto.builder().skillPattern("skill11").build();

        List<User> except = List.of(
                User.builder().skills(List.of(
                        Skill.builder().title("Skill10").build(),
                        Skill.builder().title("Skill11").build(),
                        Skill.builder().title("Skill12").build(),
                        Skill.builder().title("Skill13").build(),
                        Skill.builder().title("Skill14").build()
                )).build()
        );

        List<User> actual = userFilter.apply(users, userFilterDto)
                .toList();

        Assertions.assertEquals(except, actual);
    }

    @Test
    void applySkillFilterIsNull() {
        Stream<User> users = IntStream.range(0, 10)
                .boxed()
                .map(i -> User.builder()
                        .skills(IntStream.range(0, 5)
                                .boxed()
                                .map(j -> Skill.builder().title("Skill%d%d".formatted(i, j)).build())
                                .toList()
                        )
                        .build()
                );

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
                        .skills(IntStream.range(0, 5)
                                .boxed()
                                .map(j -> Skill.builder().title("Skill%d%d".formatted(i, j)).build())
                                .toList()
                        )
                        .build()
                );

        List<User> except = List.of();
        List<User> actual = userFilter.apply(users, null)
                .toList();

        Assertions.assertEquals(except, actual);
    }
}