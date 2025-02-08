package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserRegisterDto;
import school.faang.user_service.dto.user.UserResponseRegisterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AvatarService avatarService;
    private final CountryRepository countryRepository;

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

    private User saveUser(User user, Pair<String, String> avatars) {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(avatars.getLeft());
        userProfilePic.setSmallFileId(avatars.getRight());

        user.setUserProfilePic(userProfilePic);
        return userRepository.save(user);
    }
}

