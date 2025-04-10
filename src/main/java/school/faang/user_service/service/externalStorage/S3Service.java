package school.faang.user_service.service.externalStorage;

import org.springframework.lang.Nullable;
import school.faang.user_service.dto.externalStorage.ExternalResourceDto;

import java.io.InputStream;

public interface S3Service {
    String getResourceKey(String folder, String fileName);
    ExternalResourceDto uploadFile(
            InputStream data,
            long streamSize,
            @Nullable String contentType,
            String filename,
            String folder,
            String resourceKey);
}
