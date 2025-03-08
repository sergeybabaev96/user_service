package school.faang.user_service.service.career;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validation.CareerValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class CareerService {
    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final CareerMapper careerMapper;

    public CareerDto addCareer(long userId, CareerDto careerDto) {
       CareerValidator.validate(careerDto);
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new DataValidationException(String.format("User not found")));
        Career career = careerMapper.toCareer(careerDto);
        career.setUser(user);
        Career careerSave = careerRepository.save(career);
        return careerMapper.toCareerDto(careerSave);
    }

    public CareerDto updateCareer(long userId, CareerDto careerDto) {
        CareerValidator.validate(careerDto);
        Career career = careerRepository
                .findById(careerDto.getId())
                .orElseThrow(() -> new DataValidationException(String.format("Career not found")));

        if (career.getUser().getId() != userId) {
            log.error("User {} can only update their own career", userId);
            throw new DataValidationException("User can only update their own career");
        }
        career.setDateFrom(careerDto.getFrom());
        career.setDateTo(careerDto.getTo());
        career.setCompany(careerDto.getCompany());
        career.setPosition(careerDto.getPosition());

        Career saveCareer = careerRepository.save(career);
        return careerMapper.toCareerDto(saveCareer);
    }

    public CareerDto getById(long careerId) {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new DataValidationException(String.format("Career not found")));
        return careerMapper.toCareerDto(career);
    }
}