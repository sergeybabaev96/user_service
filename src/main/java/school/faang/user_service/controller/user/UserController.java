package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "USERS", description = "Get users")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Search for a user",
            description = "Allows you to find a user by his id"
    )
    @GetMapping("/{userId}")
    public ResponseEntity<UserViewDto> getUser(@PathVariable @NotNull
                                               @Parameter(description = "User ID",
                                                       required = true, example = "12789")
                                               long userId) {

        UserViewDto user = userService.getUser(userId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(user);
    }

    @Operation(
            summary = "Search for users",
            description = "Allows you to find users by their id"
    )
    @PostMapping
    public ResponseEntity<List<UserViewDto>> getUsersByIds(@RequestBody @NonNull List<Long> ids) {
        List<UserViewDto> users = userService.getUsersByIds(ids);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(users);
    }
}
