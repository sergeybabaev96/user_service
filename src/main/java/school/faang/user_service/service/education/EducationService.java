package school.faang.user_service.service.education;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.education.EducationDto;

public interface EducationService {
    @Transactional
    EducationDto addEducation(@NotNull long userId, @Valid EducationDto educationDto);

    @Transactional
    EducationDto updateEducation(@NotNull long userId, @Valid EducationDto educationDto);

    EducationDto getById(long educationId);
}
