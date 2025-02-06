package school.faang.user_service.service.profilePicture;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.DataValidationException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class AvatarService {
    private final AmazonS3 amazonS3;
    private final String bucketName;

    public AvatarService(AmazonS3 amazonS3, @Value("${services.s3.bucketName}") String bucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }

    public String uploadAvatar(MultipartFile file) throws IOException {
        return uploadToS3(file, "profilePic/-" + System.currentTimeMillis() + "-" + file.getOriginalFilename(), file.getInputStream());
    }

    public String uploadThumbnailAvatar(MultipartFile file) throws IOException {
        byte[] thumbnailBytes = ImageCompressor.compressToThumbnail(file, 150, 150);

        String filename = "profilePic/thumbnails/-" + System.currentTimeMillis() + "-" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/png");
        metadata.setContentLength(thumbnailBytes.length);

        try (InputStream thumbnailInputStream = new ByteArrayInputStream(thumbnailBytes)) {
            amazonS3.putObject(bucketName, filename, thumbnailInputStream, metadata);
        }

        return amazonS3.getUrl(bucketName, filename).toString();
    }



    private String uploadToS3(MultipartFile file, String filename, InputStream inputStream) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());

        if (inputStream.markSupported()) {
            inputStream.mark((int) file.getSize());
        }

        long contentLength = file.getSize();
        if (contentLength <= 0) {
            throw new DataValidationException("Размер файла неверный: " + contentLength);
        }
        metadata.setContentLength(contentLength);

        try {
            amazonS3.putObject(bucketName, filename, inputStream, metadata);
        } catch (Exception e) {
            throw new IOException("Ошибка загрузки файла в хранилище S3", e);
        } finally {
            inputStream.close();
        }

        return amazonS3.getUrl(bucketName, filename).toString();
    }
}