package school.faang.user_service.controller.career;

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
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.service.career.CareerService;
import school.faang.user_service.validator.career.CareerValidator;

@RestController
@RequestMapping("/careers")
@RequiredArgsConstructor
public class CareerController {
    private final CareerService careerService;

    @PostMapping
    public ResponseEntity<CareerDto> addCareer(
            @RequestParam("userId") long userId,
            @RequestBody CareerDto careerDto) {
        CareerValidator.validate(careerDto);
        CareerDto addCareer = careerService.addCareer(userId, careerDto);
       return ResponseEntity.ok(addCareer);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<CareerDto> updateCareer(
            @PathVariable long userId,
            @RequestBody CareerDto careerDto) {
        CareerValidator.validate(careerDto);
        CareerDto updateCareer = careerService.updateCareer(userId,careerDto);
        return ResponseEntity.ok(updateCareer);
    }

    @GetMapping("/{careerId}")
    public ResponseEntity<CareerDto> getById(
            @PathVariable long careerId) {
        CareerDto getById = careerService.getById(careerId);
        return ResponseEntity.ok(getById);
    }
}