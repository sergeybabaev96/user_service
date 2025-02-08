package school.faang.user_service.service.external;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.AppConfig;
import school.faang.user_service.service.external.abs.AvatarUploadingSystem;

import java.io.*;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioAvatarService implements AvatarUploadingSystem {
    private final AppConfig appConfig;
    private final MinioClient minioClient;

    public String uploadToMinio(ByteArrayOutputStream stream, String contentType) {
        String filename = "profile-" + LocalDateTime.now() + ".jpg";
        String bucketName = appConfig.getAvatarBucketName();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(stream.toByteArray());

        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(filename)
                .stream(byteArrayInputStream, stream.size(), -1)
                .contentType(contentType)
                .build();

        try {
            minioClient.putObject(putObjectArgs);
            return filename;
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return "";
    }

    public byte[] getImageFromMinio(String fileId) {
        String bucketName = appConfig.getAvatarBucketName();
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(fileId)
                .build();

        try (InputStream inputStream = minioClient.getObject(getObjectArgs)) {
            return inputStream.readAllBytes();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return new byte[0];
    }

    public void deleteFile(String fileId) {
        if (StringUtils.isBlank(fileId)) {
            return;
        }
        String bucketName = appConfig.getAvatarBucketName();
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileId)
                    .build());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void resizeImage(MultipartFile multipartFile,
                             int maxSize,
                             OutputStream stream) throws IOException {
        Thumbnails.of(multipartFile.getInputStream())
                .size(maxSize, maxSize)
                .toOutputStream(stream);
    }
}
