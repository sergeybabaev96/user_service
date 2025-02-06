package school.faang.user_service.service.profilePicture;

import org.springframework.web.multipart.MultipartFile;
import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageCompressor {

    public static byte[] compressToThumbnail(MultipartFile avatarFile, int width, int height) throws IOException {
        try (InputStream inputStream = avatarFile.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Thumbnails.of(inputStream)
                    .size(width, height)
                    .outputFormat("png")
                    .toOutputStream(outputStream);

            byte[] compressedBytes = outputStream.toByteArray();

            if (compressedBytes.length == 0) {
                throw new IOException("Миниатюра не может быть пустой");
            }

            return compressedBytes;
        }
    }
}
