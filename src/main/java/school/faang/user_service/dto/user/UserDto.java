package school.faang.user_service.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.model.PreferredContact;

import java.util.Locale;

/**
 * DTO с базовой информацией о пользователе.
 * <p>
 * Используется для межсервисного взаимодействия.
 * </p>
 */
@Data
@Builder
@Schema(description = "Basic user information for inter-service communication")
public class UserDto {
    @Schema(
            description = "Unique identifier of the user",
            example = "12789"
    )
    private Long id;

    @Schema(
            description = "Username (display name)",
            example = "john_doe"
    )
    private String username;

    @Schema(
            description = "User's email address",
            example = "user@example.com"
    )
    private String email;

    @Schema(
            description = "User's phone number",
            example = "+1234567890"
    )
    private String phone;

    @Schema(
            description = "Preferred contact method",
            example = "EMAIL")
    private PreferredContact preference = PreferredContact.EMAIL;

    @Schema(
            description = "User's locale",
            example = "Locale.ENGLISH"
    )
    private Locale locale = Locale.ENGLISH;
}