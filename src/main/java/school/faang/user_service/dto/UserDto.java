package school.faang.user_service.dto;

import lombok.Builder;

@Builder
public record UserDto(long id, String username, String email, String phone, long telegramId, PreferredContact preference, boolean active) {

    public enum PreferredContact {
        EMAIL, PHONE, TELEGRAM
    }
}
