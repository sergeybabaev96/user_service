package school.faang.user_service.dto;

import lombok.Data;

@Data
public class EducationViewDto {
    private Long id;
    private Integer yearFrom;
    private Integer yearTo;
    private String institution;
    private String educationLevel;
    private String specialization;
}
