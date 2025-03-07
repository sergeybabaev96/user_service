package school.faang.user_service.controller.career;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.service.career.CareerService;

@Component
@AllArgsConstructor
public class CareerController {
    private final CareerService careerService;

    public CareerDto careerDto(long userId, CareerDto careerDto) {
        return careerService.addCareer(userId, careerDto);
    }

    public CareerDto updateCareer(long userId, CareerDto careerDto) {
        return careerService.updateCareer(userId, careerDto);
    }

    public CareerDto getById(long careerId) {
        return careerService.getById(careerId);
    }
}