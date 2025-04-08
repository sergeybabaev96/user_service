package school.faang.user_service.service.education;

import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;

public interface EducationService {
    EducationDto addEducation(long userId, EducationDto educationDto);
    EducationDto updateEducation(long userId, EducationDto educationDto);
    Education getEducationById(long educationId);
}