package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDto {

    @NotNull(message = "Username can't be null")
    protected String username;

    @NotNull(message = "Password can't be null")
    protected String password;

    @Email
    @NotNull(message = "Email can't be null")
    protected String email;

    protected String phone;

    protected String aboutMe;

    protected boolean active;

    protected String city;

    private long countryId;

    private Integer experience;
}
