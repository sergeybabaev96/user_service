package school.faang.user_service.dto.person;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactInfo {
    @NotBlank(message = "Title should not be blank")
    @Email(message = "Please provide a valid email address")
    @Size(max = 64, message = "The length must not exceed 64 characters")
    String email;

    @NotBlank(message = "Title should not be blank")
    @Size(max = 32, message = "The length must not exceed 32 characters")
    String phone;

    @JsonUnwrapped
    Address address;
}