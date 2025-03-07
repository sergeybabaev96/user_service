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
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.validation.UserServiceValidation;

@Service
@RequiredArgsConstructor
public class EducationService {

    private static final String USER_NULL = "User not found";

    private final EducationRepository educationRepository;
    private final UserRepository userRepository;
    private final EducationMapper educationMapper;

    @Transactional
    public EducationDto addEducation(long userId, EducationDto educationDto) {
        UserServiceValidation.yearFromEducationValidCurrentDate(educationDto.getYearFrom());
        User user = userRepository.findById(userId).orElseThrow(() -> new DataValidationException(USER_NULL));
        Education education = educationMapper.toEducation(educationDto);
        education.setUser(user);
        Education saveEducation = educationRepository.save(education);
        return educationMapper.toEducationDto(saveEducation);
    }

}
