package school.faang.user_service.service.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filters.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.adapter.UserRepositoryAdapter;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserService userService;

    private List<UserFilter> filters;

    @Mock
    private UserRepository mockUserRepo;

    @Mock
    private UserMapper mockUserMapper;

    @Mock
    private User mockFirstUser;

    @Mock
    private User mockSecondUser;

    @Mock
    private UserRepositoryAdapter userRepositoryAdapter;

    private UserDto dto = new UserDto();
    private User user = new User();

    @BeforeEach
    void init() {
        dto.setId(1L);
        dto.setUsername("John");
        dto.setEmail("john@gmail.com");

        user.setId(1L);
        user.setUsername("John");
        user.setEmail("john@gmail.com");

        UserFilter mockFirstUserFilter = Mockito.mock(UserFilter.class);
        UserFilter mockSecondUserFilter = Mockito.mock(UserFilter.class);
        filters = List.of(mockFirstUserFilter, mockSecondUserFilter);

        userService = new UserService(mockUserRepo, filters, mockUserMapper, userRepositoryAdapter);
    }

    @Test
    void testGetPremiumUsers() {
        UserFilterDto userFilterDto = new UserFilterDto();

        UserDto firstUserDto = new UserDto();

        userFilterDto.setCityPattern("Moscow");
        userFilterDto.setNamePattern("Maxim");

        List<User> users = List.of(mockFirstUser, mockSecondUser);

        Mockito.when(filters.get(0).apply(mockUserRepo.findPremiumUsers().toList(), userFilterDto)).thenReturn(users);
        Mockito.when(filters.get(0).isApplicable(userFilterDto)).thenReturn(true);
        Mockito.when(mockUserMapper.toDto(mockFirstUser)).thenReturn(firstUserDto);

        userService.getPremiumUsers(userFilterDto);

        Mockito.verify(filters.get(0), Mockito.times(1)).isApplicable(userFilterDto);
        ArgumentCaptor<List<User>> listUsers = ArgumentCaptor.forClass(List.class);
        Mockito.verify(filters.get(0), Mockito.times(1)).apply(listUsers.capture(), Mockito.eq(userFilterDto));
    }

    @Test
    @DisplayName("Test must return user when id is exist")
    void testGetUserByIdSuccess() {
        Mockito.when(userRepositoryAdapter.getById(1L)).thenReturn(user);
        Mockito.when(mockUserMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.getUserById(1L);

        Assertions.assertEquals(dto.getUsername(), result.getUsername());
        Assertions.assertEquals(dto, result);
    }

    @Test
    @DisplayName("Test must return exception when user not exist data base")
    void testGetUserByIdFailed() {
        Long userId = 1L;
        Mockito.when(userRepositoryAdapter.getById(userId))
                .thenThrow(new EntityNotFoundException("User not found with id: " + userId));

        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    @DisplayName("Test must return users when id is exist")
    void testGetUsersByIdsSuccess() {
        Long userId = 1L;
        List<Long> userIds = List.of(userId);

        List<UserDto> listDto = List.of(dto);

        List<User> listUser = List.of(user);

        Mockito.when(userRepositoryAdapter.getUsersByIds(userIds)).thenReturn(listUser);
        Mockito.when(mockUserMapper.toListDto(listUser)).thenReturn(listDto);

        List<UserDto> result = userService.getUsersByIds(userIds);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("John", result.get(0).getUsername());
        Assertions.assertEquals(listDto, result);
    }
}
