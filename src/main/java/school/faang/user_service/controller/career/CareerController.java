package school.faang.user_service.controller.career;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.service.career.CareerService;

@Controller
@RequiredArgsConstructor
public class CareerController {
    private final CareerService careerService;

    @PostMapping
    public CareerDto addCareer(long userId, CareerDto careerDto) {
        return careerService.addCareer(userId, careerDto);
    }

    @PutMapping
    public CareerDto updateCareer(long userId, CareerDto careerDto) {
        return careerService.updateCareer(userId, careerDto);
    }

    @GetMapping
    public CareerDto getById(long careerId) {
        return careerService.getById(careerId);
    }
}
