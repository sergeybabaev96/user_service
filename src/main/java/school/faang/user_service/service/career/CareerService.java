package school.faang.user_service.service.career;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;

@Service
public class CareerService {
    private final CareerRepository careerRepository;
    private final UserRepository userRepository;
    private final CareerMapper careerMapper;

    public CareerService(UserRepository userRepository,
                         CareerRepository careerRepository,
                         CareerMapper careerMapper) {
        this.careerMapper = careerMapper;
        this.userRepository = userRepository;
        this.careerRepository = careerRepository;
    }

    public CareerDto addCareer(long userId, CareerDto careerDto) {
        if (!careerDto.getFrom().isBefore(LocalDate.now())) {
            throw new DataValidationException("Career date can not be in the future");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Career partial = careerMapper.toCareer(careerDto);
        Career career = Career.builder()
                .dateFrom(partial.getDateFrom())
                .dateTo(partial.getDateTo())
                .company(partial.getCompany())
                .position(partial.getPosition())
                .user(user)
                .build();

        Career result = careerRepository.save(career);
        return careerMapper.toCareerDto(result);
    }

    public CareerDto updateCareer(long userId, CareerDto careerDto) {
        if (!careerDto.getFrom().isBefore(LocalDate.now())) {
            throw new DataValidationException("Career date can not be in the future");
        }
        Career careerToUpdate = careerRepository.findById(careerDto.getId())
                .orElseThrow(() -> new DataValidationException("Career not found"));
        if (!careerToUpdate.getUser().getId().equals(userId)) {
            throw new DataValidationException("Career user id not match");
        }
        Career partial = careerMapper.toCareer(careerDto);
        Career updated = Career.builder()
                .id(careerToUpdate.getId())
                .dateFrom(partial.getDateFrom())
                .dateTo(partial.getDateTo())
                .company(partial.getCompany())
                .position(partial.getPosition())
                .user(careerToUpdate.getUser())
                .build();

        Career result = careerRepository.save(updated);
        return careerMapper.toCareerDto(result);
    }

    public CareerDto getById(long careerId) {
        Career career = careerRepository.findById(careerId).orElseThrow(() ->
                new RuntimeException("Career not found"));
        return careerMapper.toCareerDto(career);
    }
}
