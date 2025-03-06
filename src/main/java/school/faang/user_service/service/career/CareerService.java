package school.faang.user_service.service.career;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.validation.DataValidationException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CareerService {
    private UserRepository userRepository;
    private CareerRepository careerRepository;
    private CareerMapper careerMapper;

    public CareerDto addCareer(long userId, CareerDto careerDto) {
        validateCareer(careerDto);
        User user = userRepository.findById(userId).orElseThrow(() -> new DataValidationException("User not found"));
        Career career = careerMapper.toCareer(careerDto);
        career.setUser(user);
        Career careerSave = careerRepository.save(career);
        return careerMapper.toCareerDto(careerSave);
    }

    public CareerDto updateCareer(long userId, CareerDto careerDto) {
        validateCareer(careerDto);
        Career career = careerRepository.findById(careerDto.getId())
                .orElseThrow(() -> new DataValidationException("Career not found"));

        if (career.getUser().getId() != userId) {
            log.error("User {} can only update their own career", userId);
            throw new DataValidationException("User can only update their own career");
        }
        Career updateCareer = careerMapper.toCareer(careerDto);
        updateCareer.setUser(career.getUser());

        Career saveCareer = careerRepository.save(updateCareer);
        return careerMapper.toCareerDto(saveCareer);
    }

    public CareerDto getById(long careerId) {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new DataValidationException("Career not found"));
        return careerMapper.toCareerDto(career);
    }

    private void validateCareer(CareerDto careerDto) {
        if(careerDto.getFrom().isBefore(LocalDate.now())) {
            log.error("Date {} is not correct", careerDto.getFrom());
            throw new DataValidationException("Date is not correct");
        }
    }
}
