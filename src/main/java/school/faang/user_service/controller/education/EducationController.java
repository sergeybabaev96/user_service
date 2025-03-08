package school.faang.user_service.controller.education;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.education.EducationService;

@RestController
@RequestMapping("/education")
@RequiredArgsConstructor
public class EducationController {
    private final EducationService educationService;

    @PostMapping("/{userId}")
    public EducationDto addEducation(@PathVariable long userId, @Valid @RequestBody EducationDto educationDto) {
        return educationService.addEducation(userId, educationDto);
    }

    @PutMapping("/{userId}")
    public EducationDto updateEducation(@PathVariable long userId, @Valid @RequestBody EducationDto educationDto) {
        return educationService.updateEducation(userId, educationDto);
    }

    @GetMapping("/{educationId}")
    public EducationDto getById(@PathVariable long educationId) {
        return educationService.getById(educationId);
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
