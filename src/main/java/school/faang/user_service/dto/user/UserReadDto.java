package school.faang.user_service.dto.user;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
@Builder
public class UserReadDto {
    private Long id;
    private String username;
    private String email;
    private boolean active;
    private PreferredContact preference;
    private Long telegramChatId;
}
