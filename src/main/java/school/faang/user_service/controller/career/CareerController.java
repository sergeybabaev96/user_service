package school.faang.user_service.controller.career;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.service.career.CareerService;
import school.faang.user_service.validation.CareerValidator;

@Controller
@RequestMapping("/careers")
@RequiredArgsConstructor
public class CareerController {
    private CareerService careerService;

    @PostMapping("/{userId}")
    public CareerDto addCareer(long userId, CareerDto careerDto) {
        CareerValidator.validate(careerDto);
       return careerService.addCareer(userId, careerDto);
    }

    @PutMapping("/{userId}")
    public CareerDto updateCareer(long userId, CareerDto careerDto) {
        CareerValidator.validate(careerDto);
        return careerService.updateCareer(userId,careerDto);
    }

    @GetMapping("/{careerId}")
    public CareerDto getById(long careerId) {
        return careerService.getById(careerId);
    }
}