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

    public EducationDto addEducation(long userId, @NonNull EducationDto educationDto) throws DataValidationException {

        if (educationDto.getYearFrom() > Year.now().getValue()) {
            throw new DataValidationException("Год начала обучения не может быть меньше текущего года");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("Ошибка: пользователь с ID {} не найден", userId);
            return new DataValidationException("User is not found");
        });

        Education education = educationMapper.toEducation(educationDto);
        education.getUser().setId(user.getId());

        Education updatedEducation = educationRepository.save(education);

        return educationMapper.toEducationDto(updatedEducation);
    }

}
