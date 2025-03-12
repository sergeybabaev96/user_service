package school.faang.user_service.service.education;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EducationCreateDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.Year;

@Slf4j
@Service
@RequiredArgsConstructor
public class EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    public EducationCreateDto addEducation(long userId, @NonNull EducationCreateDto educationDto) throws DataValidationException {
        validateYearFrom(educationDto);

        User user = findUser(userId);

        Education education = educationMapper.toEducation(educationDto);
        education.setUser(user);

        Education updatedEducation = educationRepository.save(education);
        return educationMapper.toEducationDto(updatedEducation);
    }

    public EducationCreateDto updateEducation(long userId, @NonNull EducationCreateDto educationDto) throws DataValidationException {
        validateYearFrom(educationDto);
        Education education = educationMapper.toEducation(getById(educationDto.getId()));

        validateUser(userId, education.getUser().getId());
        User user = findUser(userId);
        education.setUser(user);

        Education updatedEducation = educationRepository.save(education);
        return educationMapper.toEducationDto(updatedEducation);
    }

    public EducationCreateDto getById(long educationId) throws DataValidationException {

        Education education = educationRepository.findById(educationId).orElseThrow(() -> {
            log.error("Ошибка: данные об образовании по ID {} не найдены", educationId);
            return new DataValidationException("Education is not found");
        });
        return educationMapper.toEducationDto(education);
    }

    private void validateYearFrom(@NonNull EducationCreateDto educationDto) throws DataValidationException {
        if (educationDto.getYearFrom() > Year.now().getValue()) {
            log.error("Ошибка: год начала обучения не может быть больше текущего года");
            throw new DataValidationException("The start date of studies is too late");
        }
    }

    private void validateUser(long userId, long userIdInEntity) throws DataValidationException {
        if (userId != userIdInEntity) {
            log.error("Ошибка: пользователь с ID {} пытается обновить информацию по другому пользователю", userId);
            throw new DataValidationException("User tried update other user's data");
        }
    }

    private User findUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("Ошибка: пользователь с ID {} не найден", userId);
            return new DataValidationException("User is not found");
        });
    }
}
