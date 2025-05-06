package school.faang.user_service.handler.goal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.controller.goal.GoalController;
import school.faang.user_service.dto.goal.GoalErrorResponseDto;
import school.faang.user_service.exception.goal.CountActiveGoalMoreMaxException;
import school.faang.user_service.exception.goal.GoalNotFoundException;
import school.faang.user_service.exception.user.UserNotFoundException;

@ControllerAdvice(assignableTypes = {GoalController.class})
@Slf4j
public class GoalExceptionHandler {
    @ExceptionHandler(GoalNotFoundException.class)
    public ResponseEntity<GoalErrorResponseDto> handleGoalNotFoundException(GoalNotFoundException ex) {
        GoalErrorResponseDto response = createErrorResponse(ex, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(CountActiveGoalMoreMaxException.class)
    public ResponseEntity<GoalErrorResponseDto> handleCountActiveGoalMoreMax(CountActiveGoalMoreMaxException ex) {
        GoalErrorResponseDto response = createErrorResponse(ex, HttpStatus.CONFLICT);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<GoalErrorResponseDto> handleUserNotFoundException(UserNotFoundException ex) {
        GoalErrorResponseDto response = createErrorResponse(ex, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    private GoalErrorResponseDto createErrorResponse(Throwable ex, HttpStatus status) {
        log.error("Error in GoalController: {}, response status {}", ex.getMessage(), status);
        return new GoalErrorResponseDto(ex.getMessage());
    }
}
