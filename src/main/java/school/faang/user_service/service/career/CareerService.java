package school.faang.user_service.service.career;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CareerService {
    private final CareerRepository careerRepository;
    private final UserRepository userRepository;
    private final CareerMapper careerMapper;

    public CareerDto addCareer(long userId, CareerDto careerDto) {
        if (!careerDto.getFrom().isBefore(LocalDate.now())) {
            throw new DataValidationException("Career date can not be in the future");
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new DataValidationException("User not found"));
        Career career = careerMapper.toCareer(careerDto);
        career.setUser(user);
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
        Career updated = careerMapper.toCareer(careerDto);
        updated.setId(careerToUpdate.getId());
        updated.setUser(careerToUpdate.getUser());
        Career result = careerRepository.save(updated);
        return careerMapper.toCareerDto(result);
    }

    public CareerDto getById(long careerId) {
        Career career = careerRepository.findById(careerId).orElseThrow(() ->
                new RuntimeException("Career not found"));
        return careerMapper.toCareerDto(career);
    }
}
