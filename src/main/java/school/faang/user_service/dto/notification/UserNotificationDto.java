package school.faang.user_service.dto.notification;

import lombok.Data;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
public class UserNotificationDto {
    private long id;
    private String username;
    private String email;
    private String phone;
    private Long chatId;
    private PreferredContact preference;
}
