package school.faang.user_service.controller.education;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.service.education.EducationService;

@RestController
@RequestMapping("/api/educations")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @PostMapping
    public ResponseEntity<EducationDto> addEducation(
            @RequestParam("userId") long userId,
            @RequestBody EducationDto educationDto) {
        EducationDto savedEducation = educationService.addEducation(userId, educationDto);
        return ResponseEntity.ok(savedEducation);
    }

    @PutMapping("/userId={userId}/education")
    public ResponseEntity<EducationDto> updateEducation(
            @PathVariable long userId,
            @RequestBody EducationDto educationDto) {
        EducationDto updatedEducation = educationService.updateEducation(userId, educationDto);
        return ResponseEntity.ok(updatedEducation);
    }

    @GetMapping
    public ResponseEntity<EducationDto> getById(@RequestParam("id") long educationId) {
        EducationDto educationDto = educationService.getById(educationId);
        return ResponseEntity.ok(educationDto);
    }
}
