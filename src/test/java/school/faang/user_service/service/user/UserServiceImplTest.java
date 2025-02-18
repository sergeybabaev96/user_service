package school.faang.user_service.service.user;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.filter.user.UserFilterDto;
import school.faang.user_service.dto.user.UserRegisterDto;
import school.faang.user_service.dto.user.UserResponseRegisterDto;
import school.faang.user_service.filter.user.UserCountryFilter;
import school.faang.user_service.filter.user.UserIsActiveFilter;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.user.UserPrepareData.*;

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

    private final List<Filter<User, UserFilterDto>> userFilters = new ArrayList<>();

    private UserServiceImpl userService;

    @BeforeEach
    public void init() {
        userFilters.add(new UserCountryFilter());
        userFilters.add(new UserIsActiveFilter());
        userService = new UserServiceImpl(userRepository, mapper, avatarService, countryRepository, userFilters);
    }

    @Test
    public void testRegisterUser() {
        Pair<String, String> avatarsIds = Pair.of("random-1", "random-2");
        when(countryRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getCountry()));
        when(avatarService.saveRandomAvatarsToS3(any())).thenReturn(avatarsIds);
        when(userRepository.save(any())).thenReturn(getUser());
        UserRegisterDto userRegisterDto = getUserRegisterDto();

        UserResponseRegisterDto result = userService.registerUser(userRegisterDto);

        assertEquals(mapper.toResponseRegisterDto(getUser()), result);
    }

    @Test
    public void testGetAllUsersByFilters() {
        int pageNumber = 0;
        int pageSize = 10;
        UserFilterDto filters = new UserFilterDto(1L, null);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> userPage = new PageImpl<>(List.of(getUser()));
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        List<UserDto> result = userService.getAllUsersByFilters(pageNumber, pageSize, filters);

        assertEquals(1, result.size());
        assertEquals(getUserDto(), result.get(0));

        verify(userRepository).findAll(pageable);
    }

    @Test
    public void testGetPremiumUsersByFilters() {
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> userPage = new PageImpl<>(List.of(getUser()));
        when(userRepository.findPremiumUsers(pageable)).thenReturn(userPage);

        UserFilterDto filters = new UserFilterDto(1L, null);
        List<UserDto> premiumUsers = userService.getPremiumUsersByFilters(pageNumber, pageSize, filters);

        verify(userRepository).findPremiumUsers(pageable);
        assertEquals(1, premiumUsers.size());
    }
}