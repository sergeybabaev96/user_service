package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.CreateUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ExternalResourceNotFoundException;
import school.faang.user_service.exception.ExternalServiceError;
import school.faang.user_service.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping()
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto userDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(userService.createUser(userDto));
        } catch (DataValidationException ex) {
            return ResponseEntity.badRequest().build();
        } catch (ExternalResourceNotFoundException | ExternalServiceError ex) {
            return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).build();
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
