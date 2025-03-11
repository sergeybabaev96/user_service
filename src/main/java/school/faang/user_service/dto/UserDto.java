package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.contact.PreferredContact;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserDto {
    private Long id;
    private String username;
    private String telegramUsername;
    private String telegramChatId;
    private PreferredContact preference;
    private String email;
    private String aboutMe;
    private String country;
    private Integer experience;
}
