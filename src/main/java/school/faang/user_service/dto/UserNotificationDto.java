package school.faang.user_service.dto;

import school.faang.user_service.entity.contact.PreferredContact;

/**
 * DTO for {@link school.faang.user_service.entity.User}
 */
public record UserNotificationDto(Long id, String username, String email, String phone,
                                  PreferredContact preference) {
}
