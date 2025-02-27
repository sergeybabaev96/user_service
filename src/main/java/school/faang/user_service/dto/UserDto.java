package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.contact.PreferredContact;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserDto {
    private long id;
    private String username;
    private String telegramUsername;
    private String telegramChatId;
    private PreferredContact preference;
    private String email;
}
