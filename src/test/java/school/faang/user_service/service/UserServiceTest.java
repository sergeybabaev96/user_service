package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private List<UserDto> userDtoList;
    private Stream<User> userStream;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        userStream = Stream.of(
                User.builder()
                        .id(1L).username("name1").email("email1").build(),
                User.builder()
                        .id(2L).username("name2").email("email2").build()
        );

        userDtoList = List.of(
                new UserDto(1L, "name1", "email1"),
                new UserDto(2L, "name2", "email2")
        );
    }

    @Test
    void getPremiumUsers_WhenFilterIsNull_ReturnAllUserDtos() {
       when(userRepository.findPremiumUsers()).thenReturn(userStream);

        List<UserDto> result = userService.getPremiumUsers(null);

        assertEquals(userDtoList, result);
    }
}