package school.faang.user_service.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
        ContactInfo contactInfo;

        @JsonUnwrapped
        Education education;

        String employer;

        @Data
        public static class ContactInfo {
                @NotBlank(message = "Title should not be blank")
                @Email(message = "Please provide a valid email address")
                @Size(max = 64, message = "The length must not exceed 64 characters")
                String email;
                @NotBlank(message = "Title should not be blank")
                @Size(max = 32, message = "The length must not exceed 32 characters")
                String phone;

                @JsonUnwrapped
                Address address;

                @Data
                public static class Address {
                        String street;
                        @NotBlank(message = "Title should not be blank")
                        @Size(max = 64, message = "The length must not exceed 64 characters")
                        String city;
                        String state;
                        @NotBlank(message = "Title should not be blank")
                        @Size(max = 64, message = "The length must not exceed 64 characters")
                        String country;
                        String postalCode;
                }
        }

        @Data
        public static class Education {
                String faculty;
                String yearsOfStudy;
                String major;
                String GPA;
        }
}
