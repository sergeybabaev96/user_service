package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String folder, int maxSize) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());

            BufferedImage resizedImage = Thumbnails.of(image)
                    .size(maxSize, maxSize)
                    .keepAspectRatio(true)
                    .outputFormat("jpg")
                    .asBufferedImage();

            ByteArrayOutputStream outputStreamResizedImage = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", outputStreamResizedImage);
            byte[] bytes = outputStreamResizedImage.toByteArray();
            ByteArrayInputStream inputStreamResizedImage = new ByteArrayInputStream(bytes);

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(bytes.length);
            objectMetadata.setContentType("image/jpeg");
            String key = String.format("%s/%d_%dpx_%s", folder, System.currentTimeMillis(), maxSize,
                    file.getOriginalFilename());
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, inputStreamResizedImage, objectMetadata);
            s3Client.putObject(putObjectRequest);
            return key;
        } catch (IOException e) {
            log.error("Error: ", e);
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public InputStream downloadFile(String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(bucketName, key);
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

}
