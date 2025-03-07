package school.faang.user_service.service.s3.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.s3.S3Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private static final String BUCKET_NAME = "user-profile-pics-bucket";

    private final AmazonS3 amazonS3;

    @Override
    public String uploadFile(MultipartFile file) {
        log.info("Uploading file to S3 bucket: {}", BUCKET_NAME);

        String key = String.format("%s/%d%s", BUCKET_NAME, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            createBucketIfNotExist();
            PutObjectResult putObjectResult = amazonS3.putObject(getPutObjectRequest(file, key));

            log.info("File uploaded to S3, ETag: {}, key: {}", putObjectResult.getETag(), key);
        } catch (RuntimeException e) {
            log.error("Error while uploading file to S3", e);
            throw e;
        }
        return key;
    }

    private void createBucketIfNotExist() {
        if (!amazonS3.doesBucketExistV2(BUCKET_NAME)) {
            amazonS3.createBucket(BUCKET_NAME);
        }
    }

    private PutObjectRequest getPutObjectRequest(MultipartFile file, String key) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            return new PutObjectRequest(BUCKET_NAME, key, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
