package school.faang.user_service.controller.education;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.EducationCreateDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.education.EducationService;

@Controller
@RequiredArgsConstructor
public class EducationController {
    private final EducationService educationService;

    public EducationCreateDto addEducation(long userId, @NonNull EducationCreateDto educationDto) throws DataValidationException {
        return educationService.addEducation(userId, educationDto);
    }

    public EducationCreateDto updateEducation(long userId, @NonNull EducationCreateDto educationDto) throws DataValidationException {
        return educationService.updateEducation(userId, educationDto);
    }

    public EducationCreateDto getById(long educationId) throws DataValidationException {
        return educationService.getById(educationId);
    }
}
