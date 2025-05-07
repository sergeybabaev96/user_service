package school.faang.user_service.controller.career;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.service.career.CareerService;

@Controller
public class CareerController {
    private final CareerService careerService;

    @Autowired
    public CareerController(CareerService careerService) {
        this.careerService = careerService;
    }

    public CareerDto addCareer(long userId, CareerDto careerDto) {
        return careerService.addCareer(userId, careerDto);
    }

    public CareerDto updateCareer(long userId, CareerDto careerDto) {
        return careerService.updateCareer(userId, careerDto);
    }

    public CareerDto getById(long careerId) {
        return careerService.getById(careerId);
    }
}
