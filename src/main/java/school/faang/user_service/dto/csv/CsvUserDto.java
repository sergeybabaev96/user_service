package school.faang.user_service.dto.csv;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CsvUserDto {

    @JsonProperty("firstName")
    @NotBlank(message = "FirstName is required")
    private String firstName;

    @JsonProperty("lastName")
    @NotBlank(message = "LastName is required")
    private String lastName;

    @JsonProperty("yearOfBirth")
    @NotNull(message = "Year of birth is required")
    @Min(value = 1900, message = "Year must be after 1900")
    @Max(value = 2025, message = "Year must not exceed 2025")
    private Integer yearOfBirth;

    @JsonProperty("group")
    @NotBlank(message = "Group is required")
    private String group;

    @JsonProperty("studentID")
    @NotBlank(message = "Student ID is required")
    private String studentId;

    @JsonProperty("email")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @JsonProperty("phone")
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Phone must be 10–15 digits with optional '+'")
    private String phone;

    @JsonProperty("street")
    @NotBlank(message = "Street is required")
    private String street;

    @JsonProperty("city")
    @NotBlank(message = "City is required")
    private String city;

    @JsonProperty("state")
    @NotBlank(message = "State is required")
    private String state;

    @JsonProperty("country")
    @NotBlank(message = "Country is required")
    private String country;

    @JsonProperty("postalCode")
    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "\\d{4,10}", message = "Postal code must be 4–10 digits")
    private String postalCode;

    @JsonProperty("faculty")
    @NotBlank(message = "Faculty is required")
    private String faculty;

    @JsonProperty("yearOfStudy")
    @NotNull(message = "Year of study is required")
    private Integer yearOfStudy;

    @JsonProperty("major")
    @NotBlank(message = "Major is required")
    private String major;

    @JsonProperty("GPA")
    @NotNull(message = "GPA is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "GPA must be at least 0.0")
    @DecimalMax(value = "4.0", inclusive = true, message = "GPA must be no more than 4.0")
    private Double GPA;

    @JsonProperty("status")
    @NotBlank(message = "Status is required")
    private String status;

    @JsonProperty("admissionDate")
    @NotNull(message = "Admission date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate admissionDate;

    @JsonProperty("graduationDate")
    @NotNull(message = "Graduation date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate graduationDate;

    @JsonProperty("degree")
    @NotBlank(message = "Degree is required")
    private String degree;

    @JsonProperty("institution")
    @NotBlank(message = "Institution is required")
    private String institution;

    @JsonProperty("completionYear")
    @NotNull(message = "Completion year is required")
    private Integer completionYear;

    @JsonProperty("scholarship")
    @NotNull(message = "Scholarship is required")
    private Boolean scholarship;

    @JsonProperty("employer")
    @NotBlank(message = "Employer is required")
    private String employer;
}
