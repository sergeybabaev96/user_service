package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRegistrationDto {

    @NotBlank
    private String username;

    @Email
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private Long countryId;
}
