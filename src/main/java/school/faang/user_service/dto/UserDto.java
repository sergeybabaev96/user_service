package school.faang.user_service.dto;

import lombok.Builder;

@Builder
public record UserDto(long id, String username, String email, String phone, long telegramId, PreferredContact preference) {

    public enum PreferredContact {
        EMAIL, PHONE, TELEGRAM
    }
}
