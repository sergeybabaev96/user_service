package school.faang.user_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("yearOfBirth")
    private String yearOfBirth;
    @JsonProperty("group")
    private String group;
    @JsonProperty("studentID")
    private String studentId;
    @JsonProperty("email")
    private String email;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("street")
    private String street;
    @JsonProperty("city")
    private String city;
    @JsonProperty("state")
    private String state;
    @JsonProperty("country")
    private String country;
    @JsonProperty("postalCode")
    private String postalCode;
    @JsonProperty("faculty")
    private String faculty;
    @JsonProperty("yearOfStudy")
    private String yearOfStudy;
    @JsonProperty("major")
    private String major;
    @JsonProperty("GPA")
    private String gpa;
    @JsonProperty("status")
    private String status;
    @JsonProperty("admissionDate")
    private String admissionDate;
    @JsonProperty("graduationDate")
    private String graduationDate;
    @JsonProperty("degree")
    private String degree;
    @JsonProperty("institution")
    private String institution;
    @JsonProperty("completionYear")
    private String completionYear;
    @JsonProperty("scholarship")
    private String scholarship;
    @JsonProperty("employer")
    private String employer;
}
