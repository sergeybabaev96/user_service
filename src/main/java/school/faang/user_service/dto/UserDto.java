package school.faang.user_service.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UserDto(
        long id,
        String username,
        String email,
        String phone,
        long telegramId,
        PreferredContact preference,
        boolean active,
        List<Long> skills
) {
    public enum PreferredContact {
        EMAIL, PHONE, TELEGRAM
    }
}
