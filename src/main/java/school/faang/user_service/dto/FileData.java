package school.faang.user_service.dto;

import lombok.Builder;

import java.io.InputStream;

@Builder
public record FileData(
        InputStream content,
        String contentType
) {
}
