package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.contact.PreferredContact;

/**
 * DTO for {@link school.faang.user_service.entity.User}
 */
public record UserRegisterRequest(
        @NotBlank @Size(max = 64) String username,
        @NotBlank @Size(max = 64) String email,
        @Size(max = 32) String phone,
        @NotBlank @Size(max = 128) String password,
        @NotNull @Positive Long countryId,
        @Size String city,
        PreferredContact preferredContact) {
}