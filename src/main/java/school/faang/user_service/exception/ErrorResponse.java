package school.faang.user_service.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

/***
 * Класс для наполнения тела ответа в случае,
 * если сработал глобальный обработчик исключений GlobalExceptionHandler
 */

@Data
public class ErrorResponse {
    private String message;
    private HttpStatus httpStatus;

    public ErrorResponse(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

}