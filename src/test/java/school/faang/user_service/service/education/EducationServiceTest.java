package school.faang.user_service.service.education;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
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

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EducationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EducationRepository educationRepository;

    @Spy
    private EducationMapper educationMapper;

    @InjectMocks
    private EducationService educationService;

    @Captor
    private ArgumentCaptor<Long> userIdCaptor;

    @Captor
    private ArgumentCaptor<EducationDto> educationDtoCaptor;

    @Captor
    private ArgumentCaptor<Education> educationCaptor;

    @Captor
    private ArgumentCaptor<Long> educationIdCaptor;

    private final Integer MINUS_ONE_YEARS = LocalDate.now().minusYears(1).getYear();

    private final long userId = 1L;
    private final long educationId = 2L;

    private final EducationDto educationDto = new EducationDto();
    private final Education education = new Education();
    private final User user = new User();
    private final Education existingEducation = new Education();

    @BeforeEach
    public void setUp() {
        educationDto.setYearFrom(MINUS_ONE_YEARS);
        education.setId(educationId);
        educationDto.setId(educationId);
        user.setId(userId);
    }

    @Test
    public void testUserNotFound() {


        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> {
            educationService.addEducation(userId, educationDto);
        });
    }

    @Test
    public void testAddEducationSuccess() {

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(educationMapper.toEducation(educationDto)).thenReturn(education);
        when(educationRepository.save(education)).thenReturn(education);
        when(educationMapper.toEducationDto(education)).thenReturn(educationDto);

        EducationDto result = educationService.addEducation(userId, educationDto);

        verify(userRepository, times(1)).findById(userIdCaptor.capture());
        verify(educationMapper, times(1)).toEducation(educationDtoCaptor.capture());
        verify(educationRepository, times(1)).save(educationCaptor.capture());
        verify(educationMapper, times(1)).toEducationDto(educationCaptor.capture());

        assertNotNull(result);
        assertEquals(educationDto, result);
        assertEquals(userId, userIdCaptor.getValue());
        assertEquals(educationDto, educationDtoCaptor.getValue());

    }

    @Test
    public void testUpdateEducation_Success() {

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
        verify(userRepository, times(1)).findById(userIdCaptor.capture());
        verify(educationRepository, times(1)).findById(educationIdCaptor.capture());
        verify(educationMapper, times(1)).toEducation(educationDtoCaptor.capture());
        verify(educationRepository, times(1)).save(educationCaptor.capture());
        verify(educationMapper, times(1)).toEducationDto(educationCaptor.capture());

        assertEquals(userId, userIdCaptor.getValue());
        assertEquals(educationId, educationIdCaptor.getValue());
        assertEquals(educationDto, educationDtoCaptor.getValue());
        assertEquals(updatedEducation, educationCaptor.getValue());
    }

    @Test
    public void testUpdateEducation_EducationNotFound() {


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(educationRepository.findById(educationId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> {
            educationService.updateEducation(userId, educationDto);
        });

        verify(userRepository, times(1)).findById(userIdCaptor.capture());
        verify(educationRepository, times(1)).findById(educationIdCaptor.capture());
        verify(educationMapper, never()).toEducation(any());
        verify(educationRepository, never()).save(any());
        verify(educationMapper, never()).toEducationDto(any());

        assertEquals(userId, userIdCaptor.getValue());
        assertEquals(educationId, educationIdCaptor.getValue());
    }

    @Test
    public void testUpdateEducation_InvalidUser() {


        existingEducation.setId(educationId);
        existingEducation.setUser(new User());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(educationRepository.findById(educationId)).thenReturn(Optional.of(existingEducation));

        assertThrows(DataValidationException.class, () -> {
            educationService.updateEducation(userId, educationDto);
        });

        verify(userRepository, times(1)).findById(userIdCaptor.capture());
        verify(educationRepository, times(1)).findById(educationIdCaptor.capture());
        verify(educationMapper, never()).toEducation(any());
        verify(educationRepository, never()).save(any());
        verify(educationMapper, never()).toEducationDto(any());

        assertEquals(userId, userIdCaptor.getValue());
        assertEquals(educationId, educationIdCaptor.getValue());

    }

    @Test
    public void testGetById_Success() {

        when(educationRepository.findById(educationId)).thenReturn(Optional.of(education));
        when(educationMapper.toEducationDto(education)).thenReturn(educationDto);

        EducationDto result = educationService.getById(educationId);

        assertNotNull(result);
        assertEquals(educationDto, result);
        verify(educationRepository, times(1)).findById(educationIdCaptor.capture());
        verify(educationMapper, times(1)).toEducationDto(educationCaptor.capture());

        assertEquals(educationId, educationIdCaptor.getValue());
        assertEquals(education, educationCaptor.getValue());
    }

    @Test
    public void testGetById_EducationNotFound() {

        when(educationRepository.findById(educationId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> {
            educationService.getById(educationId);
        });

        verify(educationRepository, times(1)).findById(educationIdCaptor.capture());
        verify(educationMapper, never()).toEducationDto(any());

        assertEquals(educationId, educationIdCaptor.getValue());
    }
}