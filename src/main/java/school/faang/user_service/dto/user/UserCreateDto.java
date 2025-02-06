package school.faang.user_service.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserCreateDto {
    @NotEmpty(message = "Имя не может быть пустым")
    @Size(max = 64, message = "Имя не должно быть длиннее 64 символов")
    private String username;

    @NotEmpty(message = "Почта не может быть пустой")
    @Email(message = "Некорректный формат почты")
    private String email;

    @NotEmpty(message = "Телефон не может быть пустым")
    @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Телефон должен содержать от 10 до 15 цифр и может начинаться с +")
    private String phone;

    @NotEmpty(message = "Пароль не может быть пустым")
    @Length(min = 8, max = 128, message = "Пароль должен содержать от 8 до 128 символов")
    private String password;

    private String confirmPassword;

    @AssertTrue(message = "Необходимо согласиться с условиями")
    private boolean agreeToTerms;

    @NotNull
    private Long countryId;

    private MultipartFile profilePic;
}
