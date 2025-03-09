package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.NonNull;
import school.faang.user_service.entity.contact.PreferredContact;

@Builder
public record UserDto(
        @NonNull
        Long id,
        @NotEmpty(message = "Имя не может быть пустым")
        @Max(value = 64, message = "Имя не должно быть длиннее 64 символов")
        String username,
        @Email
        String email,
        PreferredContact preference
) {
}
