package school.faang.user_service.service.resource;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ResourseService {

    public byte[] resize(byte[] imageBytes, int maxSize, String format) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(inputStream)
                .size(maxSize, maxSize)
                .outputFormat(format)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }
}