package school.faang.user_service.csv.model.person;

import lombok.Data;

@Data
public class PreviousEducation {
    private String degree;
    private String institution;
    private String completionYear;
}
