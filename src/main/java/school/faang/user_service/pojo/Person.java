package school.faang.user_service.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Person {
    private String firstName;
    private String lastName;
    private String yearOfBirth;
    private String group;
    @JsonProperty("studentID")
    private String studentId;
    private String email;
    private String phone;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String faculty;
    private String yearOfStudy;
    private String major;
    @JsonProperty("GPA")
    private String gpa;
    private String status;
    private String admissionDate;
    private String graduationDate;
    private String degree;
    private String institution;
    private String completionYear;
    private String scholarship;
    private String employer;

    public String toString() {
        return String.format(
                        "State: " + state + ";" +
                        "Faculty: " + faculty + ";" +
                        "Year of study: " + yearOfStudy + ";" +
                        "Major: " + major + ";" +
                        "Employer: " + employer);
    }
}
