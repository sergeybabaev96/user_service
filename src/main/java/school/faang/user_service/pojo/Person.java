package school.faang.user_service.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Person {
    @JsonProperty("firstName")
    String firstName;
    @JsonProperty("lastName")
    String lastName;
    @JsonProperty("yearOfBirth")
    String yearOfBirth;
    @JsonProperty("group")
    String group;
    @JsonProperty("studentID")
    String studentID;
    @JsonProperty("email")
    String email;
    @JsonProperty("phone")
    String phone;
    @JsonProperty("street")
    String street;
    @JsonProperty("city")
    String city;
    @JsonProperty("state")
    String state;
    @JsonProperty("country")
    String country;
    @JsonProperty("postalCode")
    String postalCode;
    @JsonProperty("faculty")
    String faculty;
    @JsonProperty("yearOfStudy")
    String yearOfStudy;
    @JsonProperty("major")
    String major;
    @JsonProperty("GPA")
    String GPA;
    @JsonProperty("status")
    String status;
    @JsonProperty("admissionDate")
    String admissionDate;
    @JsonProperty("graduationDate")
    String graduationDate;
    @JsonProperty("degree")
    String degree;
    @JsonProperty("institution")
    String institution;
    @JsonProperty("completionYear")
    String completionYear;
    @JsonProperty("scholarship")
    String scholarship;
    @JsonProperty("employer")
    String employer;
}
