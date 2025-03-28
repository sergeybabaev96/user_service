package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserViewDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Пользователи", description = "Получение пользователей")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Operation(
            summary = "Поиск пользователя",
            description = "Позволяет найти пользователя по его id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь найден",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = UserViewDto.class)))),
                    @ApiResponse(responseCode = "404", description = "Пользователи не найдены",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject("NOT_FOUND")))
            }
    )
    @GetMapping("/{userId}")
    UserViewDto getUser(@PathVariable @NotNull
                        @Parameter(description = "Идентификатор пользователя",
                                required = true, example = "12789")
                        long userId) {
        log.info("Запрос на получение данных по пользователю с ID: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("Ошибка: Не удалось найти пользователя с ID {}", userId);
            return new DataValidationException("Пользователь не найден");
        });

        log.info("Найден пользователь с ID: {}", userId);
        return userMapper.toViewDto(user);
    }

    @Operation(
            summary = "Поиск пользователей",
            description = "Позволяет найти пользователей по их id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователи найдены",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = UserViewDto.class)))),
                    @ApiResponse(responseCode = "404", description = "Пользователи не найдены",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject("NOT_FOUND")))

            }
    )
    @PostMapping
    List<UserViewDto> getUsersByIds(@RequestBody @NonNull List<Long> ids) {
        log.info("Запрос на получение данных по пользователям с ID's: {}", ids);
        List<User> users = userRepository.findAllById(ids);

        log.info("Найдены пользователя с ID's: {}", ids);
        return users.stream().map(userMapper::toViewDto).toList();
    }
}
