package school.faang.user_service.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.entity.error.DefaultErrorResponse;
import school.faang.user_service.exception.goal.AddedSkillNotExistException;
import school.faang.user_service.exception.goal.MaxActiveGoalPerUserException;

@RestControllerAdvice
public class GoalExceptionHandler {

    @ExceptionHandler(MaxActiveGoalPerUserException.class)
    public ResponseEntity<DefaultErrorResponse> handleMaxGoalLimit(MaxActiveGoalPerUserException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new DefaultErrorResponse("GOAL_LIMIT", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultErrorResponse> handleEmptyGoalTitle(MethodArgumentNotValidException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new DefaultErrorResponse("EMPTY_GOAL_NAME", "Empty goal name not allowed!"));
    }

    @ExceptionHandler(AddedSkillNotExistException.class)
    public ResponseEntity<DefaultErrorResponse> handleNotExistingSkill(AddedSkillNotExistException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new DefaultErrorResponse("SKILL_NOT_EXIST", exception.getMessage()));
    }
}