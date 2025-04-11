package school.faang.user_service.dto.publisher;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для события подписки пользователя.
 * <p>
 * Содержит информацию о подписчике, целевом пользователе и времени события.
 * </p>
 */
@Data
@AllArgsConstructor
public class FollowerEventDto {
    private long followerId;
    private long followeeId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime timestamp;
}