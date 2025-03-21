package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.avatar.UserAvatarProperties;

import javax.annotation.processing.FilerException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;
    private final UserAvatarProperties avatarProperties;

    public void uploadFile(InputStream fileToUpload, String key, long fileSize, String contentType)
            throws FilerException {

        if (!s3Client.doesBucketExistV2(avatarProperties.getBucketName())) {
            s3Client.createBucket(avatarProperties.getBucketName());
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(contentType);
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    avatarProperties.getBucketName(), key, fileToUpload, objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new FilerException("Can't save file");
        }
    }

    public InputStream downloadFile(String key) {
        GetObjectRequest getObjectArgs = new GetObjectRequest(avatarProperties.getBucketName(), key);
        S3Object object = s3Client.getObject(getObjectArgs);
        if (object == null) {
            throw new EntityNotFoundException("Image not found");
        }
        return object.getObjectContent();
    }

    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(avatarProperties.getBucketName(), key);
        s3Client.deleteObject(deleteObjectRequest);
    }
}
