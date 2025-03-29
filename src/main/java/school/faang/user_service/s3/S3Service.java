package school.faang.user_service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.FileException;

/**
 * Сервис для работы с Amazon S3 (или S3-совместимым хранилищем).
 * <p>
 * Предоставляет основные операции для управления файлами:
 * <ul>
 *   <li>{@link #uploadFile(MultipartFile, String)} - загрузка файла</li>
 *   <li>{@link #downloadFile(String)} - скачивание файла</li>
 *   <li>{@link #deleteFile(String)} - удаление файла</li>
 * </ul>
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucket;

    /**
     * Загружает файл в S3 хранилище.
     *
     * @param file загружаемый файл (не может быть null)
     * @param folder путь к папке в S3 (например, "users/123/avatars")
     * @return ключ (путь) к загруженному файлу в S3
     * @throws FileException если произошла ошибка при загрузке
     *
     */
    public String uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%s", folder, file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucket, key, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new FileException(exception.getMessage());
        }
        return key;
    }

    /**
     * Скачивает файл из S3 хранилища.
     *
     * @param fileId ключ (путь) к файлу в S3 (например, "users/123/avatars/profile.jpg")
     * @return InputStream с содержимым файла
     */
    public Resource downloadFile(String fileId) {
        try {
            S3Object s3Object = s3Client.getObject(bucket, fileId);

            return new S3Resource(s3Object, fileId);
        } catch (Exception e) {
            throw new FileException(e.getMessage());
        }
    }

    /**
     * Удаляет файл из S3 хранилища.
     *
     * @param fileId ключ (путь) к файлу в S3 (например, "users/123/avatars/profile.jpg")
     */
    public void deleteFile(String fileId) {
        try {
            s3Client.deleteObject(bucket, fileId);
        } catch (Exception e) {
            throw new FileException(e.getMessage());
        }
    }
}