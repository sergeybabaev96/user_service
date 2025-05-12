package school.faang.user_service.client.controller.education;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.client.service.education.EducationService;
import school.faang.user_service.dto.EducationDto;

@RestController
@RequiredArgsConstructor
public class EducationController {
    private final EducationService educationService;

    @PostMapping("/addEducation")
    public EducationDto addEducation(long userId, EducationDto educationDto) {
        return educationService.addEducation(userId, educationDto);
    }

    @PostMapping("/updateEducation")
    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        return educationService.updateEducation(userId, educationDto);
    }

    @PostMapping("/getById")
    public EducationDto getById(long educationId) {
        return educationService.getById(educationId);
    }
}
