package school.faang.user_service.dto.education;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class EducationDto {
    private long id;
    private Integer yearFrom;
    private Integer yearTo;
    private String institution;
    private String educationLevel;
    private String specialization;
}