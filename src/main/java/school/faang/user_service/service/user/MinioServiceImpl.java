package school.faang.user_service.service.user;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.UploadMinioException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    @SneakyThrows
    public String uploadToMinio(ByteArrayResource file, String bucketName) {
        checkBucketExists(bucketName);
        String avatarId = UUID.randomUUID().toString();
        try {
            InputStream stream = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(avatarId + ".png")
                    .stream(stream, stream.available(), -1)
                    .build());
        } catch (IOException e) {
            throw new UploadMinioException("Failed to upload avatar");
        }
        return avatarId;
    }

    @SneakyThrows
    public InputStream downloadFile(String bucketName, String key) {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(key)
                .build());
    }

    @SneakyThrows
    private void checkBucketExists(String bucketName) {
        boolean isBucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());
        if (!isBucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        }
    }
}
