package school.faang.user_service.service.education;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EducationDto;
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

    private static void yearFromValidation(@NonNull EducationDto educationDto) throws DataValidationException {
        if (educationDto.getYearFrom() > Year.now().getValue()) {
            log.error("Ошибка: год начала обучения не может быть меньше текущего года");
            throw new DataValidationException("The start date of studies is too early");
        }
    }

    private static void updateEducationValidation(long userId, long userIdInEntity) throws DataValidationException {
        if (userId != userIdInEntity) {
            log.error("Ошибка: пользователь с ID {} пытается обновить информацию по другому пользователю", userId);
            throw new DataValidationException("User tried update other user's data");
        }
    }

    public EducationDto addEducation(long userId, @NonNull EducationDto educationDto) throws DataValidationException {
        yearFromValidation(educationDto);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("Ошибка: пользователь с ID {} не найден", userId);
            return new DataValidationException("User is not found");
        });

        Education education = educationMapper.toEducation(educationDto);
        education.getUser().setId(user.getId());

        Education updatedEducation = educationRepository.save(education);
        return educationMapper.toEducationDto(updatedEducation);
    }

    public EducationDto updateEducation(long userId, @NonNull EducationDto educationDto) throws DataValidationException {
        yearFromValidation(educationDto);
        Education education = educationMapper.toEducation(getById(educationDto.getId()));

        updateEducationValidation(userId, education.getUser().getId());
        Education updatedEducation = educationMapper.toEducation(educationDto);
        updatedEducation.getUser().setId(education.getUser().getId());

        return educationMapper.toEducationDto(educationRepository.save(updatedEducation));
    }

    public EducationDto getById(long educationId) throws DataValidationException {

        Education education = educationRepository.findById(educationId).orElseThrow(() -> {
            log.error("Ошибка: данные об образовании по ID {} не найдены", educationId);
            return new DataValidationException("Education is not found");
        });
    }


}
