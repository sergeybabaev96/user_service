package school.faang.user_service.pojo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Person {
    String firstName;
    String lastName;
    Integer yearOfBirth;
    String group;
    String studentID;
    String email;
    String phone;
    String street;
    String city;
    String state;
    String country;
    String postalCode;
    String faculty;
    Integer yearOfStudy;
    String major;
    String GPA;
    String status;
    LocalDate admissionDate;
    String graduationDate;
    String degree;
    String institution;
    Integer completionYear;
    String scholarship;
    String employer;
}
