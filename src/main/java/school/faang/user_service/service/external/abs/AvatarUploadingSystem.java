package school.faang.user_service.service.external.abs;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public interface AvatarUploadingSystem {
    String uploadToMinio(ByteArrayOutputStream stream, String contentType);

    byte[] getImageFromMinio(String fileId);

    void deleteFile(String fileId);

    void resizeImage(MultipartFile multipartFile, int maxSize, OutputStream stream) throws IOException;
}
