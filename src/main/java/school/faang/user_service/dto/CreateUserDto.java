package school.faang.user_service.dto;

import org.springframework.lang.NonNull;

public record CreateUserDto(
        @NonNull String username,
        @NonNull String email,
        @NonNull String password,
        @NonNull String countryTitle) {
}
