package school.faang.user_service.dto.person;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Person {
        @JsonProperty("firstName")
        @NotBlank(message = "Title should not be blank")
        @Size(max = 32, message = "The length must not exceed 32 characters")
        private String firstName;

        @JsonProperty("lastName")
        @NotBlank(message = "Title should not be blank")
        @Size(max = 32, message = "The length must not exceed 32 characters")
        private String lastName;

        @JsonUnwrapped
        private ContactInfo contactInfo;

        @JsonUnwrapped
        private Education education;

        private String employer;
}
