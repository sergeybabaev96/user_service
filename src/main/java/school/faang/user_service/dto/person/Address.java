package school.faang.user_service.dto.person;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Address {
    private String street;

    @NotBlank(message = "Title should not be blank")
    @Size(max = 64, message = "The length must not exceed 64 characters")
    private String city;
    private String state;

    @NotBlank(message = "Title should not be blank")
    @Size(max = 64, message = "The length must not exceed 64 characters")
    private String country;
    private String postalCode;
}
