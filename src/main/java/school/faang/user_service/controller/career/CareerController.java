package school.faang.user_service.controller.career;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.service.career.CareerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/career")
public class CareerController {
    public final CareerService careerService;

    @PostMapping("/{userId}")
    public CareerDto addCareer(@PathVariable long userId, @RequestBody CareerDto careerDto) {
        return careerService.addCareer(userId, careerDto);
    }

    @PutMapping("/userId")
    public CareerDto updateCareer(@PathVariable long userId, @RequestBody CareerDto careerDto) {
        return careerService.updateCareer(userId, careerDto);
    }

    @GetMapping("/carrerId")
    public CareerDto getById(@PathVariable long carrerId) {
        return careerService.getById(carrerId);
    }
}
