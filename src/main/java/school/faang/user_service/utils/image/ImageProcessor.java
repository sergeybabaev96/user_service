package school.faang.user_service.utils.image;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ImageProcessor {

    public ImageData resizeImage(MultipartFile file, int maxSize) throws IOException {
        String extension = getFileExtension(file.getOriginalFilename());
        byte[] proccessedImage = processImage(file.getBytes(), maxSize, extension);

        return new ImageData(
                new ByteArrayInputStream(proccessedImage),
                file.getContentType(),
                proccessedImage.length
        );
    }

    private byte[] processImage(byte[] original, int maxSize, String format) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(new ByteArrayInputStream(original))
                .size(maxSize, maxSize)
                .keepAspectRatio(true)
                .outputFormat(format)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    @Getter
    @RequiredArgsConstructor
    public static class ImageData {
        private final InputStream inputStream;
        private final String contentType;
        private final long contentLength;
    }
}
