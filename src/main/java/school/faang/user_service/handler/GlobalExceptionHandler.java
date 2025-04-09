package school.faang.user_service.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

   /* @ExceptionHandler(DataRetrievalFailureException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public  Map<String, String> handleUserNotFound(DataRetrievalFailureException e) {
         Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        return response;
    }*/


    @ExceptionHandler(DataRetrievalFailureException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public  String handleUserNotFound(DataRetrievalFailureException e) {
        log.error("++handler UserService");
        return e.getMessage();
    }
}
