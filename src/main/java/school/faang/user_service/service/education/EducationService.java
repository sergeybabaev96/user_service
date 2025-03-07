package school.faang.user_service.service.education;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    @SneakyThrows
    public EducationDto addEducation(long userId, EducationDto educationDto) {
        if (educationDto.getYearFrom() >= Year.now().getValue()) {
            throw new DataValidationException("Год должен быть меньше текущего.");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new DataValidationException("Пользователь не найден."));

        Education education = educationMapper.toEducation(educationDto);
        education.setUser(user);
        education = educationRepository.save(education);
        return educationMapper.toEducationDto(education);
    }

    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        if (educationDto.getYearFrom() >= Year.now().getValue()) {
            throw new DataValidationException("Год должен быть меньше текущего.");
        }

        Education education = educationRepository.findById(educationDto.getId())
                .orElseThrow(() -> new DataValidationException("Образование не найдено."));

        if (!education.getUser().getId().equals(userId)) {
            throw new DataValidationException("Нельзя обновлять данные другого пользователя");
        }
        education.setYearFrom(educationDto.getYearFrom());
        education.setYearTo(educationDto.getYearTo());
        education.setInstitution(educationDto.getInstitution());
        education.setEducationLevel(educationDto.getEducationLevel());
        education.setSpecialization(educationDto.getSpecialization());

        education = educationRepository.save(education);
        return educationMapper.toEducationDto(education);
    }

    public EducationDto getById(long educationId) {
        Education education = educationRepository.findById(educationId).orElseThrow(() -> new DataValidationException("Образование не найдено."));
        return educationMapper.toEducationDto(education);
    }
}
