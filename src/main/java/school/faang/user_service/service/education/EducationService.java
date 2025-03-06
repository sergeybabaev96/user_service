package school.faang.user_service.service.education;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ResourceNotFoundException;
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

    public EducationDto addEducation(long userId, EducationDto educationDto) {
        validateYearFrom(educationDto.getYearFrom());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found"));

        Education education = educationMapper.toEducation(educationDto);
        education.setUser(user);

        Education savedEducation = educationRepository.save(education);
        return educationMapper.toEducationDto(savedEducation);
    }

    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        validateYearFrom(educationDto.getYearFrom());

        Education education = educationRepository.findById(educationDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Education not found"));

        if (education.getUser().getId() != userId) {
            throw new DataValidationException("User can only update their own education");
        }

        Education updatedEducation = educationMapper.toEducation(educationDto);
        updatedEducation.setUser(education.getUser());

        Education savedEducation = educationRepository.save(updatedEducation);
        return educationMapper.toEducationDto(savedEducation);
    }

    public EducationDto getById(long educationId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new ResourceNotFoundException("Education not found"));

        return educationMapper.toEducationDto(education);
    }

    private void validateYearFrom(Integer yearFrom) {
        if (yearFrom != null && yearFrom > Year.now().getValue()) {
            throw new DataValidationException("YearFrom cannot be in the future");
        }
    }
}