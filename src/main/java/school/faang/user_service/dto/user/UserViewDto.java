package school.faang.user_service.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * DTO-класс для представления информации о существующем пользователе.
 * <p>
 * Используется для передачи данных о пользователе между слоями приложения.
 * </p>
 * <p>
 * Содержит следующие поля:
 * <ul>
 *     <li>{@link #id Идентификатор пользователя}</li>
 *     <li>{@link #username Имя пользователя}</li>
 *     <li>{@link #menteesIds Идентификаторы менти,
 *     которые прикреплены к пользователю}</li>
 *     <li>{@link #mentorsIds Идентификаторы менторов,
 *     которые прикреплены к пользователю}</li>
 * </ul>
 * </p>
 *
 * @author gulnaz21
 */
@Schema(description = "Пользователь")
@Data
public class UserViewDto {
    @Schema(description = "Идентификатор пользователя", example = "12789", accessMode = Schema.AccessMode.READ_WRITE)
    private Long id;

    @Schema(description = "Фамилия пользователя", example = "Иванов", accessMode = Schema.AccessMode.READ_WRITE)
    private String username;

    @Schema(description = "Список идентификаторов менти", accessMode = Schema.AccessMode.READ_ONLY)
    private List<Long> menteesIds;

    @Schema(description = "Список идентификаторов менторов", accessMode = Schema.AccessMode.READ_ONLY)
    private List<Long> mentorsIds;
}
