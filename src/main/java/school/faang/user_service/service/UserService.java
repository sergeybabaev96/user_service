package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.constants.goal.ImageConstants;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.FileTooLargeException;
import school.faang.user_service.exception.InvalidImageFormatException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private static final int MAX_AVATAR_FILE_SIZE = 5 * 1024 * 1024;
    private static final int SIZE_FULL_AVATAR = 1080;
    private static final int SIZE_MINIATURE_AVATAR = 170;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;
    private final S3StorageService s3Service;
    private final ImageCompressorService compressorService;

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);

        if (users.isEmpty()) {
            return Collections.emptyList();
        } else {
            return users.stream()
                    .map(userMapper::toDto)
                    .toList();
        }
    }

    public void createUserAvatar(MultipartFile file) {
        log.debug("Start creating user avatar");
        if (!Objects.requireNonNull(file.getContentType()).matches(ImageConstants.IMAGE_FORMAT_REGEX)) {
            throw new InvalidImageFormatException("Invalid type file with name: %s", file.getOriginalFilename());
        }
        if (file.getSize() > MAX_AVATAR_FILE_SIZE) {
            throw new FileTooLargeException("Max avatar size - %d", MAX_AVATAR_FILE_SIZE);
        }
        User user = getUserById(userContext.getUserId());

        String fileName = generateFileKey(file.getOriginalFilename(), user.getId());
        String smallFileName = generateFileKey("small" + file.getOriginalFilename(), user.getId());

        MultipartFile largeImage = compressorService.compressImage(file, SIZE_FULL_AVATAR);
        MultipartFile smallImage = compressorService.compressImage(file, SIZE_MINIATURE_AVATAR);

        String fileKey = s3Service.uploadFile(largeImage, fileName);
        String smallFileKey = s3Service.uploadFile(smallImage, smallFileName);

        user.setUserProfilePic(createUserProfilePic(smallFileKey, fileKey));
        userRepository.save(user);
        log.info("Avatar created for user with id: {}", user.getId());
    }

    public ResponseEntity<Resource> getUserAvatar(Long userId) {
        User user = getUserById(userId);
        validateUserProfilePic(user);

        String fileName = user.getUserProfilePic().getFileId();
        InputStream fileStream = s3Service.getFile(fileName);
        String contentType = s3Service.getContentType(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(new InputStreamResource(fileStream));
    }

    public void removeUserAvatar() {
        log.debug("Start removing user avatar");
        User user = getUserById(userContext.getUserId());
        validateUserProfilePic(user);

        s3Service.deleteFile(user.getUserProfilePic().getFileId());
        s3Service.deleteFile(user.getUserProfilePic().getSmallFileId());

        user.setUserProfilePic(null);
        userRepository.save(user);
        log.info("Avatar removed for user with id: {}", user.getId());
    }

    private UserProfilePic createUserProfilePic(String smallId, String id) {
        return UserProfilePic.builder()
                .smallFileId(smallId)
                .fileId(id)
                .build();
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id %s not found", userId));
    }

    private String generateFileKey(String fileName, Long userId) {
        return userId.toString() + "-" + fileName;
    }

    private void validateUserProfilePic(User user) {
        if (user.getUserProfilePic() == null
                || user.getUserProfilePic().getFileId() == null
                || user.getUserProfilePic().getSmallFileId() == null) {
            throw new EntityNotFoundException("Avatar user with id " + user.getId() + " not found");
        }
    }
}
