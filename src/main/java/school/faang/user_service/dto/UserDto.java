package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import school.faang.user_service.entity.contact.PreferredContact;

import java.util.Locale;

@Builder
@AllArgsConstructor
@ToString
@Getter
@Setter
public class UserDto {
    private Long id;

    @NotEmpty(message = "username must not be empty")
    @NotNull(message = "username must not be null")
    private String username;

    @NotEmpty(message = "email must not be empty")
    @NotNull(message = "email must not be null")
    @Email(message = "email is not correct")
    private String email;

    private String phone;
    private PreferredContact preference;
    private Locale locale;
}