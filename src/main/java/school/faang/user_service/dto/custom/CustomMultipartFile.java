package school.faang.user_service.dto.custom;

import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Value
@Builder
@Slf4j
public class CustomMultipartFile implements MultipartFile {

    byte[] content;
    String name;
    String originalFilename;
    String contentType;

    @NotNull
    public byte[] getBytes() {
        return content;
    }

    public boolean isEmpty() {
        return content == null || content.length == 0;
    }

    public long getSize() {
        return content.length;
    }

    @NotNull
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    public void transferTo(File dest) {
        try {
            Files.write(dest.toPath(), content);
        } catch (IOException ex) {
            log.error("Transfer error. {}", ex.toString());
        }
    }
}
