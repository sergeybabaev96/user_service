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
        // Проверяем, чтобы дата начала карьеры не была в будущем
        if (careerDto.getFrom().isAfter(LocalDate.now())) {
            throw new DataValidationException("Career start can't be in the future");
        }

        // Проверяем, чтобы дата начала карьеры, компания и позиция не были пустыми
        if (careerDto.getFrom() == null
                || careerDto.getCompany() == null || careerDto.getCompany().isEmpty() || careerDto.getCompany().isBlank()
                || careerDto.getPosition() == null || careerDto.getPosition().isEmpty() || careerDto.getPosition().isBlank()) {
            throw new DataValidationException("Career fields can't be empty");
        }

        // Получаем пользователя по userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("The user with id  " + userId + " can't be found."));

        // Сохраняем данные о карьере в БД
        Career career = careerMapper.toCareer(careerDto); //перевожу обект Dto в объект Entity
        career.setUser(user); //для этого пришлось создать setter в Career (его до этого не было) - правильно ли?
        Career savedCareer = careerRepository.save(career); //сохраняю в базу данных
        return careerMapper.toCareerDto(savedCareer);
    }

    public CareerDto updateCareer(long userId, CareerDto careerDto) {
        // Проверяем, чтобы дата начала карьеры не была в будущем
        if (careerDto.getFrom().isAfter(LocalDate.now())) {
            throw new DataValidationException("Career start can't be in the future");
        }

        // Проверяем, чтобы дата начала карьеры, компания и позиция не были пустыми
        if (careerDto.getFrom() == null
                || careerDto.getCompany() == null || careerDto.getCompany().isEmpty() || careerDto.getCompany().isBlank()
                || careerDto.getPosition() == null || careerDto.getPosition().isEmpty() || careerDto.getPosition().isBlank()) {
            throw new DataValidationException("Career fields can't be empty");
        }
        Career existingCareer = careerRepository.findById(careerDto.getId())
                .orElseThrow(() -> new DataValidationException("Career with id "
                        + careerDto.getId() + " can't be found"));

        if (userId != existingCareer.getUser().getId()) {
            throw new DataValidationException("You can't change data of another user");
        }

        // Сохраняем данные о карьере в БД
        existingCareer.setDateFrom(careerDto.getFrom());
        existingCareer.setDateTo(careerDto.getTo());
        existingCareer.setCompany(careerDto.getCompany());
        existingCareer.setPosition(careerDto.getPosition());

        Career careerUpdated = careerRepository.save(existingCareer);
        return careerMapper.toCareerDto(careerUpdated);
    }

    public CareerDto getById(long careerId) {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new DataValidationException("Career with id " + careerId
                        + " can't be found"));

        return careerMapper.toCareerDto(career);
    }
}
