package school.faang.user_service.service.education;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.Year;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class EducationOperation implements EducationService {
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    @Transactional
    public EducationDto addEducation(long userId, EducationDto educationDto) {
        log.info("Adding education for userId={} with data={}", userId, educationDto);

        validateYearFrom(educationDto.getYearFrom());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found for userId={}", userId);
                    return new DataValidationException("User not found");
                });

        Education education = educationMapper.toEducation(educationDto);
        education.setUser(user);

        Education savedEducation = educationRepository.save(education);

        log.info("Education added successfully with id={}", savedEducation.getId());

        return educationMapper.toEducationDto(savedEducation);
    }

    @Transactional
    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        log.info("Updating education for userId={} with data={}", userId, educationDto);

        validateYearFrom(educationDto.getYearFrom());

        Education education = getEducationById(educationDto.getId());

        if (!Objects.equals(education.getUser().getId(), userId)) {
            log.warn("Unauthorized update attempt by userId={} for educationId={}", userId, educationDto.getId());
            throw new DataValidationException("User does not have permission to update this record");
        }

        educationMapper.updateEducationFromDto(educationDto, education);

        Education updatedEducation = educationRepository.save(education);

        log.info("Education updated successfully for educationId={}", updatedEducation.getId());

        return educationMapper.toEducationDto(updatedEducation);
    }

    public Education getEducationById(long educationId) {
        log.debug("Fetching education by id={}", educationId);
        return educationRepository.findById(educationId)
                .orElseThrow(() -> {
                    log.warn("Education record not found for id={}", educationId);
                    return new DataValidationException("Education record not found");
                });
    }

    private void validateYearFrom(int yearFrom) {
        if (yearFrom >= Year.now().getValue()) {
            log.warn("Validation failed: yearFrom={} is not less than current year", yearFrom);
            throw new DataValidationException("YearFrom must be less than the current year");
        }
    }
}