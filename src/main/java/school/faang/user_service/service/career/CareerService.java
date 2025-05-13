package school.faang.user_service.service.career;



import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.CareerValidator;


@Service
@RequiredArgsConstructor
@Transactional

public class CareerService {

    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final CareerMapper careerMapper;
    private final CareerValidator careerValidator;

    public CareerDto addCareer(long userId, CareerDto careerDto) {

        careerValidator.validate(careerDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id %s not found", userId)));

        Career career = Career.builder()
                .dateFrom(careerDto.getFrom())
                .dateTo(careerDto.getTo())
                .company(careerDto.getCompany())
                .position(careerDto.getPosition())
                .user(user)
                .build();
     return careerMapper.toCareerDto(careerRepository.save(career));
    }

    public CareerDto updateCareer(long userId, CareerDto careerDto) {

        careerValidator.validate(careerDto);

        Career career = careerRepository.findById(careerDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id %s not found", careerDto.getId())));

        if(!career.getUser().getId().equals(userId)) {
            throw new DataValidationException("Users do not match");
        }

        Career updatedCareer = career.toBuilder()
                .dateFrom(careerDto.getFrom())
                .dateTo(careerDto.getTo())
                .company(careerDto.getCompany())
                .position(careerDto.getPosition())
                .build();

        return careerMapper.toCareerDto(careerRepository.save(updatedCareer));
    }

    public CareerDto getById(long careerId) {

        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Career with id %s not found", careerId)));
        return careerMapper.toCareerDto(career);
    }
}
