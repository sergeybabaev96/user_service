package school.faang.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import school.faang.user_service.entity.contact.PreferredContact;

import java.util.List;

/**
 * DTO-класс для представления информации о существующем пользователе.
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

    @Schema(description = "Электронная почта пользователя", example = "ivanov@example.com", accessMode = Schema.AccessMode.READ_WRITE)
    private String email;

    @Schema(description = "Телефон пользователя", example = "+79991234567", accessMode = Schema.AccessMode.READ_WRITE)
    private String phone;

    @Schema(description = "Список идентификаторов менти", accessMode = Schema.AccessMode.READ_ONLY)
    private List<Long> menteesIds;

    @Schema(description = "Список идентификаторов менторов", accessMode = Schema.AccessMode.READ_ONLY)
    private List<Long> mentorsIds;

    @Schema(description = "Предпочтительный способ связи", example = "TELEGRAM", accessMode = Schema.AccessMode.READ_WRITE)
    private PreferredContact preference;
}