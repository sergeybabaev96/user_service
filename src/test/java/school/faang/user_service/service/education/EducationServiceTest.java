package school.faang.user_service.service.education;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

public class EducationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EducationRepository educationRepository;

    @Spy
    private EducationMapper educationMapper;

    @InjectMocks
    private EducationService educationService;

    private final Integer MINUS_ONE_YEARS = LocalDate.now().minusYears(1).getYear();

    private final long userId = 1L;
    private final long educationId = 2L;

    private final EducationDto educationDto = new EducationDto();
    private final Education education = new Education();
    private final User user = new User();
    private final Education existingEducation = new Education();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUserNotFound() {
        educationDto.setYearFrom(MINUS_ONE_YEARS);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> {
            educationService.addEducation(userId, educationDto);
        });
    }

    @Test
    public void testAddEducationSuccess() {
        educationDto.setYearFrom(MINUS_ONE_YEARS);

        user.setId(userId);

        education.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(educationMapper.toEducation(educationDto)).thenReturn(education);
        when(educationRepository.save(education)).thenReturn(education);
        when(educationMapper.toEducationDto(education)).thenReturn(educationDto);

        EducationDto result = educationService.addEducation(userId, educationDto);

        assertNotNull(result);
        assertEquals(educationDto, result);
        verify(userRepository, times(1)).findById(userId);
        verify(educationMapper, times(1)).toEducation(educationDto);
        verify(educationRepository, times(1)).save(education);
        verify(educationMapper, times(1)).toEducationDto(education);
    }

    @Test
    public void testUpdateEducation_Success() {

        educationDto.setId(educationId);
        educationDto.setYearFrom(MINUS_ONE_YEARS);

        user.setId(userId);

        existingEducation.setId(educationId);
        existingEducation.setUser(user);

        Education updatedEducation = new Education();
        updatedEducation.setId(educationId);
        updatedEducation.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(educationRepository.findById(educationId)).thenReturn(Optional.of(existingEducation));
        when(educationMapper.toEducation(educationDto)).thenReturn(updatedEducation);
        when(educationRepository.save(updatedEducation)).thenReturn(updatedEducation);
        when(educationMapper.toEducationDto(updatedEducation)).thenReturn(educationDto);

        EducationDto result = educationService.updateEducation(userId, educationDto);

        assertNotNull(result);
        assertEquals(educationDto, result);
        verify(userRepository, times(1)).findById(userId);
        verify(educationRepository, times(1)).findById(educationId);
        verify(educationMapper, times(1)).toEducation(educationDto);
        verify(educationRepository, times(1)).save(updatedEducation);
        verify(educationMapper, times(1)).toEducationDto(updatedEducation);
    }

    @Test
    public void testUpdateEducation_EducationNotFound() {

        educationDto.setId(educationId);
        educationDto.setYearFrom(MINUS_ONE_YEARS);

        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(educationRepository.findById(educationId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> {
            educationService.updateEducation(userId, educationDto);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(educationRepository, times(1)).findById(educationId);
        verify(educationMapper, never()).toEducation(any());
        verify(educationRepository, never()).save(any());
        verify(educationMapper, never()).toEducationDto(any());
    }

    @Test
    public void testUpdateEducation_InvalidUser() {

        educationDto.setId(educationId);
        educationDto.setYearFrom(MINUS_ONE_YEARS);

        user.setId(userId);

        existingEducation.setId(educationId);
        existingEducation.setUser(new User());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(educationRepository.findById(educationId)).thenReturn(Optional.of(existingEducation));

        assertThrows(DataValidationException.class, () -> {
            educationService.updateEducation(userId, educationDto);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(educationRepository, times(1)).findById(educationId);
        verify(educationMapper, never()).toEducation(any());
        verify(educationRepository, never()).save(any());
        verify(educationMapper, never()).toEducationDto(any());
    }

    @Test
    public void testGetById_Success() {

        education.setId(educationId);

        educationDto.setId(educationId);

        when(educationRepository.findById(educationId)).thenReturn(Optional.of(education));
        when(educationMapper.toEducationDto(education)).thenReturn(educationDto);

        EducationDto result = educationService.getById(educationId);

        assertNotNull(result);
        assertEquals(educationDto, result);
        verify(educationRepository, times(1)).findById(educationId);
        verify(educationMapper, times(1)).toEducationDto(education);
    }

    @Test
    public void testGetById_EducationNotFound() {

        when(educationRepository.findById(educationId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> {
            educationService.getById(educationId);
        });

        verify(educationRepository, times(1)).findById(educationId);
        verify(educationMapper, never()).toEducationDto(any());
    }
}