package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@NotBlank(message = "Username cannot be empty")
@NotBlank(message = "Email cannot be empty")
public class UserDto {
    private Long id;
    private String username;
    private String email;
}
