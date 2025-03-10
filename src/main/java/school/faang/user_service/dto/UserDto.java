package school.faang.user_service.dto;

import java.util.Objects;

public record UserDto(long id, String username, String email) {
}