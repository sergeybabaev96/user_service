package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.Filter;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserRegisterDto;
import school.faang.user_service.dto.user.UserResponseRegisterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AvatarService avatarService;
    private final CountryRepository countryRepository;
    private final List<Filter<User, UserFilterDto>> userFilters;

    @Override
    @Transactional
    public UserResponseRegisterDto registerUser(UserRegisterDto dto) {
        User user = userMapper.toEntity(dto);
        Country country = countryRepository.findById(dto.getCountryId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Country with id = %d doesn't exists", dto.getCountryId())));
        user.setCountry(country);
        Pair<String, String> avatars = avatarService.saveAvatarsToMinio(user);
        User savedUser = saveUser(user, avatars);
        return userMapper.toResponseRegisterDto(savedUser);
    }

    @Override
    public List<UserDto> getAllUsersByFilters(int pageNumber, int pageSize, UserFilterDto filters) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<User> users = userRepository.findAll(pageable).toList();
        for (Filter<User, UserFilterDto> filter : userFilters) {
            if (filter.isApplicable(filters)) {
                users = filter.apply(users, filters);
            }
        }
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public List<UserDto> getPremiumUsersByFilters(UserFilterDto filters) {
        List<User> users = userRepository.findPremiumUsers();
        for (Filter<User, UserFilterDto> filter : userFilters) {
            if (filter.isApplicable(filters)) {
                users = filter.apply(users, filters);
            }
        }
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    private User saveUser(User user, Pair<String, String> avatars) {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(avatars.getLeft());
        userProfilePic.setSmallFileId(avatars.getRight());

        user.setUserProfilePic(userProfilePic);
        return userRepository.save(user);
    }
}

