package school.faang.user_service.service.career;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.career.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CareerService {

    private CareerRepository careerRepository;
    private UserRepository userRepository;
    private CareerMapper careerMapper;

    public CareerDto addCareer(long userId, @Valid CareerDto careerDto) {
        LocalDateTime now = LocalDateTime.now();
        validatorDateTime(careerDto, now);
        userRepository.findById(userId);
        
    }

    private static void validatorDateTime(CareerDto careerDto, LocalDateTime now) {
        LocalDate currentDate = now.toLocalDate();
        if (careerDto.getFrom().isAfter(currentDate)) {
            throw new DataValidationException("дата не должна быть из будущего");
        }
    }
}
