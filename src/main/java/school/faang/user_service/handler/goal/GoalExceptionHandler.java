package school.faang.user_service.handler.goal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.controller.goal.GoalController;
import school.faang.user_service.dto.goal.GoalErrorResponseDto;
import school.faang.user_service.exception.goal.CountActiveGoalMoreMaxException;
import school.faang.user_service.exception.goal.CreateGoalCompletedException;
import school.faang.user_service.exception.goal.GoalAlreadyCompletedException;
import school.faang.user_service.exception.goal.GoalNotFoundException;
import school.faang.user_service.exception.skill.SkillNotFoundException;
import school.faang.user_service.exception.user.UserNotFoundException;

import java.util.stream.Collectors;

@ControllerAdvice(assignableTypes = {GoalController.class})
@Slf4j
public class GoalExceptionHandler {
    @ExceptionHandler(GoalNotFoundException.class)
    public ResponseEntity<GoalErrorResponseDto> handleGoalNotFoundException(GoalNotFoundException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CountActiveGoalMoreMaxException.class)
    public ResponseEntity<GoalErrorResponseDto> handleCountActiveGoalMoreMax(CountActiveGoalMoreMaxException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(GoalAlreadyCompletedException.class)
    public ResponseEntity<GoalErrorResponseDto> handleGoalAlreadyCompletedException(GoalAlreadyCompletedException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CreateGoalCompletedException.class)
    public ResponseEntity<GoalErrorResponseDto> handleCreateGoalCompletedException(CreateGoalCompletedException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<GoalErrorResponseDto> handleUserNotFoundException(UserNotFoundException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SkillNotFoundException.class)
    public ResponseEntity<GoalErrorResponseDto> handleSkillNotFoundException(SkillNotFoundException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GoalErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(error -> String.format("Field '%s' %s",
                        ((FieldError) error).getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        return createErrorResponse(errorMessage, HttpStatus.BAD_REQUEST);
    }


    private ResponseEntity<GoalErrorResponseDto> createErrorResponse(String errorMsg, HttpStatus status) {
        log.error("Error in GoalController: {}, response status {}", errorMsg, status);
        GoalErrorResponseDto response = new GoalErrorResponseDto(errorMsg);
        return new ResponseEntity<>(response, status);
    }
}
