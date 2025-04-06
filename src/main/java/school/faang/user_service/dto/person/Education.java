package school.faang.user_service.dto.person;

import lombok.Data;

@Data
public class Education {
    private String faculty;
    private String yearsOfStudy;
    private String major;
    private String GPA;
}