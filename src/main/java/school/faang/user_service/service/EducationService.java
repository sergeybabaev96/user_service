package school.faang.user_service.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.dto.education.EducationDto;

public interface EducationService {

    EducationDto addEducation(@NotNull long userId, @Valid EducationDto educationDto);

    EducationDto updateEducation(@NotNull long userId, @Valid EducationDto educationDto);

    EducationDto getById(long educationId);
}