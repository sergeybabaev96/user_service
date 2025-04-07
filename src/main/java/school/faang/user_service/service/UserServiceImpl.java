package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.CreateUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.ExternalServiceError;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.exception.UsernameNotFoundException;
import school.faang.user_service.exception.UsernameNotUniqueException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.avatarGenerator.AvatarGeneratorService;
import school.faang.user_service.service.externalStorage.S3Service;
import school.faang.user_service.validator.CreateUserValidator;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final AvatarGeneratorService avatarGeneratorService;
    private final S3Service s3Service;

    private final CreateUserValidator createUserValidator;

    private final UserMapper userMapper;

    @Value("user-avatars-aws-folder")
    private String userAvatarsAwsFolder;

    @Override
    public User getReferenceById(long userId) {
        return userRepository.getReferenceById(userId);
    }

    @Override
    public long findUniqueIdByUsername(String username) {
        List<Long> userIds = userRepository.findIdByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Username '%s' not found".formatted(username)));
        long userId;
        if (userIds.size() == 1) {
            userId = userIds.get(0);
        } else {
            throw new UsernameNotUniqueException("Username '%s' must be unique".formatted(username));
        }
        return userId;
    }

    @Override
    public User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with id %d not found".formatted(userId)));
    }

    @Override
    public void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("User with ID {} does not exist", userId);
            throw new UserNotFoundException("User does not exist.");
        }
    }

    @Override
    public boolean existsById(long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public UserDto getUser(long userId) {
        var user = findUserById(userId);

        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        var users = userRepository.findAllById(ids);

        return userMapper.toDtoList(users);
    }

    @Override
    @Transactional
    public UserDto createUser(CreateUserDto createUserDto) {
        createUserValidator.validateUsername(createUserDto);
        createUserValidator.validateUserEmail(createUserDto);
        var country = createUserValidator.validateCountryTitle(createUserDto);

        var resourceKey = s3Service.getResourceKey(getAvatarFilename(), userAvatarsAwsFolder);

        var user = userMapper.toEntity(createUserDto);
        var userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(resourceKey);
        user.setUserProfilePic(userProfilePic);
        user.setCountry(country);

        var savedUser = userRepository.save(user);

        uploadFileToS3Storage(resourceKey);

        return userMapper.toDto(savedUser);
    }

    private void uploadFileToS3Storage(String resourceKey) {
        var imageData = avatarGeneratorService.getRandomAvatar();
        try (var imageDataStream = imageData.asInputStream()) {
            s3Service.uploadFile(
                    imageDataStream,
                    imageData.readableByteCount(),
                    avatarGeneratorService.getRandomAvatarContentType(),
                    getAvatarFilename(),
                    userAvatarsAwsFolder,
                    resourceKey);
        } catch (Exception e) {
            var errorMessage = "Cannot create random avatar stream: %s".formatted(e.getMessage());
            log.error(errorMessage, e);
            throw new ExternalServiceError(errorMessage, e);
        }
    }

    private String getAvatarFilename() {
        return UUID.randomUUID().toString();
    }
}
