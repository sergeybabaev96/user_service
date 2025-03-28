package school.faang.user_service.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/***
 * Класс для наполнения тела ответа в случае,
 * если сработал глобальный обработчик исключений GlobalExceptionHandler
 */

@Data
public class ErrorResponse {

    @JsonFormat(pattern = "dd-MM-YYYY HH:mm:ss")
    private LocalDateTime dataTime = LocalDateTime.now();
    private int status;
    private String error;
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(String message, String error, int status) {
        this.message = message;
        this.error = error;
        this.status = status;
    }

}