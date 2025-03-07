package school.faang.user_service.controller.education;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.education.EducationService;

@Controller
@RequiredArgsConstructor
public class EducationController {
    private final EducationService educationService;

    public EducationDto addEducation(long userId, @NonNull EducationDto educationDto) throws DataValidationException {
        return educationService.addEducation(userId, educationDto);
    }

    public EducationDto updateEducation(long userId, @NonNull EducationDto educationDto) throws DataValidationException {
        return educationService.updateEducation(userId, educationDto);
    }

    public EducationDto getById(long educationId) throws DataValidationException {
        return educationService.getById(educationId);
    }
}
