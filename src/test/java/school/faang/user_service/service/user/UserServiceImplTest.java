package school.faang.user_service.service.user;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserRegisterDto;
import school.faang.user_service.dto.user.UserResponseRegisterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.user.UserPrepareData.createUser;
import static school.faang.user_service.utils.user.UserPrepareData.createUserRegisterDto;
import static school.faang.user_service.utils.user.UserPrepareData.getCountry;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl mapper;

    @Mock
    private AvatarService avatarService;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testRegisterUser() {
        User user = createUser();
        Pair<String, String> avatarsIds = Pair.of("random-1", "random-2");
        when(countryRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getCountry()));
        when(avatarService.saveAvatarsToMinio(any())).thenReturn(avatarsIds);
        when(userRepository.save(any())).thenReturn(user);

        UserRegisterDto userRegisterDto = createUserRegisterDto();

        UserResponseRegisterDto result = userService.registerUser(userRegisterDto);

        assertEquals(mapper.toResponseRegisterDto(user), result);
    }


}