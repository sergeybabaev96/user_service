package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.CreateUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.externalStorage.ExternalResourceDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ExternalServiceError;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.avatarGenerator.AvatarGeneratorService;
import school.faang.user_service.service.externalStorage.S3Service;
import school.faang.user_service.validator.CreateUserValidator;

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
    public boolean doesUserExist(long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataRetrievalFailureException(
                        "User with id %d is not found".formatted(userId)));
    }

    @Override
    public User findById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found"));
    }

    @Override
    public boolean existsById(long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public UserDto createUser(CreateUserDto createUserDto) {
        createUserValidator.validateUsername(createUserDto);
        createUserValidator.validateUserEmail(createUserDto);
        var country = createUserValidator.validateCountryTitle(createUserDto);

        var randomAvatarResourceDto = getRandomAvatarResourceDto();

        var user = userMapper.toEntity(createUserDto);
        var userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(randomAvatarResourceDto.key());
        user.setUserProfilePic(userProfilePic);
        user.setCountry(country);

        var savedUser = getSavedUser(createUserDto, user, randomAvatarResourceDto);

        return userMapper.toDto(savedUser);
    }

    private User getSavedUser(CreateUserDto createUserDto, User user, ExternalResourceDto randomAvatarResourceDto) {
        try {
            return userRepository.save(user);
        } catch (Exception ex) {
            var errorMessage = "Cannot create user %s".formatted(createUserDto.username());
            log.error(errorMessage, ex);

            try {
                s3Service.deleteFile(randomAvatarResourceDto.key());
            } catch (Exception deleteRandomAvatarFileException) {
                log.error(
                        "Cannot delete random avatar: {}",
                        deleteRandomAvatarFileException.getMessage(),
                        deleteRandomAvatarFileException);
            }

            throw ex;
        }
    }

    private ExternalResourceDto getRandomAvatarResourceDto() {
        var imageData = avatarGeneratorService.getRandomAvatar();
        try (var imageDataStream = imageData.asInputStream()) {
            return s3Service.uploadFile(
                    imageDataStream,
                    imageData.readableByteCount(),
                    avatarGeneratorService.getRandomAvatarContentType(),
                    getAvatarFilename(),
                    userAvatarsAwsFolder);
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
