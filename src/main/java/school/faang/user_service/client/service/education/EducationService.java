package school.faang.user_service.client.service.education;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;


    public EducationDto addEducation(long userId, EducationDto educationDto) {
        validateYear(educationDto);

        Education education = educationMapper.toEducation(educationDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String
                        .format("User with id %d was not found", userId)));
        education.setUser(user);

        return educationMapper.toEducationDto(educationRepository.save(education));
    }

    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        long educationId = educationDto.getId();

        if (!educationRepository.existsById(educationId)) {
            throw new NotFoundException(String.format("Education by id %d was not found!", educationId));
        }

        validateYear(educationDto);
       Education education = educationMapper.toEducation(educationDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String
                        .format("User with id %d was not found", userId)));
        education.setUser(user);

        return educationMapper.toEducationDto(educationRepository.save(education));
    }

    public EducationDto getById(long educationId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException(String
                        .format("Education with id %d was not found", educationId)));

        return educationMapper.toEducationDto(education);
    }

    private void validateYear(EducationDto educationDto) {
        if (educationDto.getYearFrom() >= LocalDate.now().getYear()) {
            throw new DataValidationException("Year of start must be early");
        }
    }
}
