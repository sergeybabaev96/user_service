package school.faang.user_service.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/***
 * Класс для наполнения тела ответа в случае,
 * если сработал глобальный обработчик исключений GlobalExceptionHandler
 */
@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {

    @JsonFormat(pattern = "dd-mm-yyyy hh:mm:ss")
    private LocalDateTime dataTime;

    private String url;
    private int status;
    private String error;
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
}