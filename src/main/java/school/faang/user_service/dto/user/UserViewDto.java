package school.faang.user_service.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * DTO для отображения информации о пользователе.
 * <p>
 * Используется для API responses.
 * </p>
 */
@Data
@Schema(description = "User information view")
public class UserViewDto {

    @Schema(
            description = "Unique identifier of the user",
            example = "12789",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "User's phone number",
            example = "+1234567890",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String phone;

    @Schema(
            description = "Username or display name",
            example = "john_doe",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String username;

    @Schema(
            description = "Years of professional experience",
            example = "5",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Integer experience;

    @Schema(
            description = "List of mentee IDs associated with this user",
            example = "[56, 78]",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private List<Long> menteesIds;

    @Schema(
            description = "List of mentor IDs associated with this user",
            example = "[34, 92]",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private List<Long> mentorsIds;
}