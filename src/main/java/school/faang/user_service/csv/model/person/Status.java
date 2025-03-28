package school.faang.user_service.csv.model.person;

import lombok.Data;

import java.util.List;

@Data
public class Status {
    private String admissionDate;
    private String graduationDate;
    private List<PreviousEducation> previousEducation;
}
