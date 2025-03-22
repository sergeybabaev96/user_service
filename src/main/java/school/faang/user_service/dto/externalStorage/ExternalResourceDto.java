package school.faang.user_service.dto.externalStorage;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record ExternalResourceDto(
        @NonNull String key,
        @NonNull BigInteger size,
        @NonNull LocalDateTime createdAt,
        @NonNull LocalDateTime updatedAt,
        @Nullable String contentType,
        @NonNull String name) {
}
