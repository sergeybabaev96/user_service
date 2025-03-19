package school.faang.user_service.service.education;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EducationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private EducationRepository educationRepository;
    @Spy
    private EducationMapper educationMapper;

    private User user = new User();
    private EducationDto educationDto;
    private EducationService educationService;

    @BeforeEach
    void setUp() {
        educationService = new EducationService(userRepository, educationRepository, educationMapper);
    }

    EducationDto createEducationDto(int year) {
        educationDto = new EducationDto();
        educationDto.setYearFrom(year);
        return educationDto;
    }

    @Test
    void testAddEducationYearFromNotExceedTheCurrentYear() {
        EducationDto educationDto = createEducationDto(2025);
        assertThrows(DataValidationException.class, () -> educationService.addEducation(1L, educationDto));
    }

    @Test
    void testAddEducationNotFindUserById() {
        EducationDto educationDto = createEducationDto(2024);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty()); //-> выдает ошибку
        assertThrows(DataValidationException.class, () -> educationService.addEducation(1L, educationDto));
    }

    @Test
    void testAddEducationSave() {
        EducationDto educationDto = createEducationDto(2024);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Education education = educationMapper.toEducationWithUser(educationDto,user);
        when(educationRepository.save(education)).thenReturn(education);
        educationService.addEducation(anyLong(), educationDto);
        verify(educationRepository, times(1)).save(education);
    }
}