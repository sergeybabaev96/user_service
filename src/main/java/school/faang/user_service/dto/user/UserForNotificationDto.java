package school.faang.user_service.dto.user;

import school.faang.user_service.dto.ContactDto;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.entity.recommendation.Language;

import java.util.List;

public record UserForNotificationDto(
        long id,
        String username,
        String email,
        String phone,
        Language locale,
        PreferredContact preference,
        List<ContactDto> contacts
) {
}
