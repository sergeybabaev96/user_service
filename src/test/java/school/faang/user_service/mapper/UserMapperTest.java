package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.subscriber.MockUsers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private MockUsers mockUsers = new MockUsers();

    @Test
    void shouldMapUserToUserDto() {
        List<UserDto> userDtoList  = userMapper.toListUserDto(mockUsers.getUsers().toList());

        assertNotNull(userDtoList);
        assertEquals(3, userDtoList.size());

        UserDto userDto = userDtoList.get(0);
        assertEquals(mockUsers.user1.getId(), userDto.getId());
        assertEquals(mockUsers.user1.getUsername(), userDto.getUsername());
        assertEquals(mockUsers.user1.getEmail(), userDto.getEmail());
    }

    @Test
    void shouldMapUserDtoToUser() {
        UserDto userDto = new UserDto();
        userDto.setId(mockUsers.user1.getId());
        userDto.setUsername(mockUsers.user1.getUsername());
        userDto.setEmail(mockUsers.user1.getEmail());

        List<User> users = userMapper.toListUser(List.of(userDto));

        assertNotNull(users);
        assertEquals(1, users.size());

        User user = users.get(0);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getUsername(), user.getUsername());
        assertEquals(userDto.getEmail(), user.getEmail());
    }
}
