package school.faang.user_service.service.external;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.S3Exception;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3Client;


    @Value("${avatar.bucketName}")
    private String bucketName;

    public void uploadToBucket(String fileName, byte[] data, String contentType){
        log.info("Uploading avatar {}", fileName);

        ensureBucketExists(bucketName);
        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentType(contentType);
        metaData.setContentLength(data.length);

        try(ByteArrayInputStream inputStream = new ByteArrayInputStream(data)){
            PutObjectResult result = amazonS3Client.putObject(bucketName,fileName, inputStream, metaData);
            log.info("File uploaded to S3: {}, ETag: {}", fileName, result.getETag());
        }catch (SdkClientException | IOException e){
            log.error("Error uploading avatar to S3", e);
            throw new S3Exception("Не удалось загрузить файл в S3", e);
        }
    }

    public URL getPresingnedUrl(String fileName){
        try{
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName,fileName);
            return amazonS3Client.generatePresignedUrl(request);
        }catch (SdkClientException e){
            log.error("Failed to generate presigned URL for: {}", fileName, e);
            throw new S3Exception("Не удалось получить ссылку на файл", e);
        }
    }
    public byte[] getFile(String fileName) {
        S3Object s3Object = amazonS3Client.getObject(bucketName, fileName);
        try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new S3Exception("Не удалось прочитать файл", e);
        }
    }

    public String getContentType(String fileName) {
        ObjectMetadata metadata = amazonS3Client.getObjectMetadata(bucketName, fileName);
        return metadata.getContentType();
    }


    private void ensureBucketExists(String bucketName){
        if(!amazonS3Client.doesBucketExistV2(bucketName)){
            amazonS3Client.createBucket(bucketName);
        }
    }


}

