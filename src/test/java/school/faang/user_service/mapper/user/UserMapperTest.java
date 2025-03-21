package school.faang.user_service.mapper.user;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserMapperTest {

    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    public void testToDto() {
        User user = User.builder()
                .id(1L)
                .mentors(Arrays.asList(
                        User.builder().id(1L).build(),
                        User.builder().id(2L).build()
                ))
                .mentees(Arrays.asList(
                        User.builder().id(3L).build(),
                        User.builder().id(4L).build()
                ))
                .build();

        UserDto userDto = userMapper.toDto(user);

        assertNotNull(userDto);
        assertEquals(1L, userDto.getId());
        assertEquals(Arrays.asList(1L, 2L), userDto.getMentorIds());
        assertEquals(Arrays.asList(3L, 4L), userDto.getMenteeIds());
    }

    @Test
    public void testUsersToUserDtos() {
        List<User> users = List.of(
                User.builder()
                        .id(1L)
                        .username("user1")
                        .email("user1@example.com")
                        .build(),
                User.builder()
                        .id(2L)
                        .username("user2")
                        .email("user2@example.com")
                        .build()
        );

        List<UserDto> userDtos = UserMapper.usersToUserDtos(users);

        assertEquals(2, userDtos.size());

        assertEquals(1L, userDtos.get(0).getId());
        assertEquals("user1", userDtos.get(0).getUsername());
        assertEquals("user1@example.com", userDtos.get(0).getEmail());

        assertEquals(2L, userDtos.get(1).getId());
        assertEquals("user2", userDtos.get(1).getUsername());
        assertEquals("user2@example.com", userDtos.get(1).getEmail());
    }

    @Test
    public void testUsersToUserDtosWithNull() {
        List<UserDto> userDtos = UserMapper.usersToUserDtos(null);

        assertTrue(userDtos.isEmpty());
    }

    @Test
    public void testUsersToUserDtosWithEmptyList() {
        List<UserDto> userDtos = UserMapper.usersToUserDtos(List.of());

        assertTrue(userDtos.isEmpty());
    }
}