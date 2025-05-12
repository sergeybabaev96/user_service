package school.faang.user_service.service.career;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CareerService {
    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final CareerMapper careerMapper;

    public CareerDto addCareer(long userId, CareerDto careerDto) {
        validateDate(careerDto.getDateFrom());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found"));

        Career career = careerMapper.toCareer(careerDto);
        career.setUser(user);
        Career savedCareer = careerRepository.save(career);
        return careerMapper.toCareerDto(savedCareer);
    }

    public CareerDto updateCareer(long userId, CareerDto careerDto) {
        validateDate(careerDto.getDateFrom());

        Career existingCareer = careerRepository.findById(careerDto.getId())
                .orElseThrow(() -> new DataValidationException("Career not found"));

        if (existingCareer.getId() != userId) {
            throw new DataValidationException("Id is not equal");
        }

        Career updateCareer = careerMapper.toCareer(careerDto);
        updateCareer.setUser(existingCareer.getUser());
        Career savedCareer = careerRepository.save(updateCareer);
        return careerMapper.toCareerDto(savedCareer);
    }

    public CareerDto getById(long careerId) {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new DataValidationException("Career not found"));
        return careerMapper.toCareerDto(career);
    }

    private void validateDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            throw new DataValidationException("Invalid date");
        }
    }
}
