package school.faang.user_service.service.avatar;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.avatar.*;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {

    private final AmazonS3 s3Client;
    private final UserRepository userRepository;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final long PRESIGNED_URL_EXPIRATION = 432_000_000L;
    private static final Set<String> SUPPORTED_FORMATS = Set.of("png", "jpg", "jpeg", "webp");

    @Transactional
    public UploadAvatarResponse uploadAvatar(Long userId, UploadAvatarRequest request) {
        MultipartFile file = request.getFile();
        validateFile(file);

        String format = getFileExtension(file);
        String filePrefix = String.format("avatars/%d/%s", userId, UUID.randomUUID());

        String fileId = filePrefix + "." + format;
        String mediumFileId = filePrefix + "_medium." + format;
        String smallFileId = filePrefix + "_small." + format;

        try {
            log.info("Uploading avatar for userId={}, format={}", userId, format);

            uploadFileToS3(file, fileId);
            byte[] mediumImage = resizeImage(file.getBytes(), 1080, format);
            uploadFileToS3(mediumImage, mediumFileId, format);
            byte[] smallImage = resizeImage(file.getBytes(), 170, format);
            uploadFileToS3(smallImage, smallFileId, format);

            UserProfilePic userProfilePic = new UserProfilePic();
            userProfilePic.setFileId(fileId);
            userProfilePic.setMediumFileId(mediumFileId);
            userProfilePic.setSmallFileId(smallFileId);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            userRepository.save(user);

            log.info("Avatar uploaded successfully for userId={}", userId);
            return new UploadAvatarResponse(fileId, mediumFileId, smallFileId);
        } catch (IOException e) {
            log.error("Failed to upload avatar for userId={}", userId, e);
            throw new RuntimeException("Failed to upload avatar", e);
        }
    }

    public GetAvatarResponse getAvatar(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserProfilePic profilePic = user.getUserProfilePic();

        if (profilePic == null || profilePic.getFileId() == null) {
            throw new IllegalArgumentException("User does not have an avatar");
        }

        log.info("Generating presigned URLs for userId={}", userId);
        return new GetAvatarResponse(
                getPresignedUrl(profilePic.getFileId()),
                getPresignedUrl(profilePic.getMediumFileId()),
                getPresignedUrl(profilePic.getSmallFileId())
        );
    }

    @Transactional
    public DeleteAvatarResponse deleteAvatar(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserProfilePic profilePic = user.getUserProfilePic();

        if (profilePic == null || profilePic.getFileId() == null) {
            return new DeleteAvatarResponse(false, "User has no avatar");
        }

        log.info("Deleting avatar for userId={}", userId);
        deleteFileFromS3(profilePic.getFileId());
        deleteFileFromS3(profilePic.getMediumFileId());
        deleteFileFromS3(profilePic.getSmallFileId());

        user.setUserProfilePic(null);
        userRepository.save(user);

        return new DeleteAvatarResponse(true, "Avatar deleted successfully");
    }

    private void validateFile(MultipartFile file) {
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed!");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum limit of 5MB!");
        }
        if (getFileExtension(file).isEmpty()) {
            throw new IllegalArgumentException("File must have an extension!");
        }
    }

    private String getFileExtension(MultipartFile file) {
        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        String format = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!SUPPORTED_FORMATS.contains(format)) {
            throw new IllegalArgumentException("Unsupported file format! Allowed: " + SUPPORTED_FORMATS);
        }
        return format;
    }

    private void uploadFileToS3(MultipartFile file, String fileId) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        s3Client.putObject(new PutObjectRequest(bucketName, fileId, file.getInputStream(), metadata));
    }

    private void uploadFileToS3(byte[] fileBytes, String fileId, String format) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/" + format);
        metadata.setContentLength(fileBytes.length);
        s3Client.putObject(bucketName, fileId, new ByteArrayInputStream(fileBytes), metadata);
    }

    private void deleteFileFromS3(String fileId) {
        s3Client.deleteObject(bucketName, fileId);
    }

    private byte[] resizeImage(byte[] imageBytes, int maxSize, String format) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(inputStream)
                .size(maxSize, maxSize)
                .outputFormat(format)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    private String getPresignedUrl(String fileId) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, fileId)
                .withExpiration(new Date(System.currentTimeMillis() + PRESIGNED_URL_EXPIRATION));
        URL url = s3Client.generatePresignedUrl(request);
        return url.toString();
    }
}