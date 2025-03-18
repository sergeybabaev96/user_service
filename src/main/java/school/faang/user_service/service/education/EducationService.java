package school.faang.user_service.service.education;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.validator.UserServiceValidation;

@Service
@RequiredArgsConstructor
public class EducationService {

    private static final String USER_NULL = "User not found";
    private static final String EDUCATION_NOT_FOUND = "Education data not found";
    private static final String INVALID_USER = "Invalid user";

    private final EducationRepository educationRepository;
    private final UserRepository userRepository;
    private final EducationMapper educationMapper;

    @Transactional
    public EducationDto addEducation(long userId, EducationDto educationDto) {
        UserServiceValidation.validYearLessCurrentYear(educationDto.getYearFrom());
        User user = userRepository.findById(userId).orElseThrow(() -> new DataValidationException(USER_NULL));
        Education education = educationMapper.toEducation(educationDto);
        education.setUser(user);
        Education saveEducation = educationRepository.save(education);
        return educationMapper.toEducationDto(saveEducation);
    }

    @Transactional
    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        UserServiceValidation.validYearLessCurrentYear(educationDto.getYearFrom());
        User user = userRepository.findById(userId).orElseThrow(() -> new DataValidationException(USER_NULL));
        Education educationForUpdate = educationRepository
                .findById(educationDto.getId())
                .orElseThrow(() -> new DataValidationException(EDUCATION_NOT_FOUND));
        if(educationForUpdate.getUser().getId() != user.getId()) {
            throw new DataValidationException(INVALID_USER);
        }
        Education education = educationMapper.toEducation(educationDto);
        education.setUser(educationForUpdate.getUser());
        return educationMapper.toEducationDto(educationRepository.save(education));
    }

    public EducationDto getById(long educationId) {
        Education education = educationRepository
                .findById(educationId)
                .orElseThrow(() -> new DataValidationException(EDUCATION_NOT_FOUND));
        return educationMapper.toEducationDto(education);
    }

}
