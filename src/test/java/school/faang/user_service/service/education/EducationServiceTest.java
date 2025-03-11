package school.faang.user_service.service.education;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.education.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.education.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.Year;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EducationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private EducationMapper educationMapper;

    @InjectMocks
    private EducationService educationService;

    private User user;
    private EducationDto educationDto;
    private Education education;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        educationDto = new EducationDto();
        educationDto.setId(1L);
        educationDto.setYearFrom(2010);
        educationDto.setYearTo(2014);
        educationDto.setInstitution("University");
        educationDto.setEducationLevel("Bachelor");
        educationDto.setSpecialization("Computer Science");

        education = new Education();
        education.setId(1L);
        education.setYearFrom(2010);
        education.setYearTo(2014);
        education.setInstitution("University");
        education.setEducationLevel("Bachelor");
        education.setSpecialization("Computer Science");
        education.setUser(user);
    }

    @Test
    void testAddEducation_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(educationMapper.toEducation(educationDto)).thenReturn(education);
        when(educationRepository.save(education)).thenReturn(education);
        when(educationMapper.toEducationDto(education)).thenReturn(educationDto);

        EducationDto result = educationService.addEducation(1L, educationDto);

        assertNotNull(result);
        assertEquals(educationDto, result);
        verify(userRepository, times(1)).findById(1L);
        verify(educationMapper, times(1)).toEducation(educationDto);
        verify(educationRepository, times(1)).save(education);
        verify(educationMapper, times(1)).toEducationDto(education);
    }

    @Test
    void testAddEducation_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> educationService.addEducation(1L, educationDto));

        verify(userRepository, times(1)).findById(1L);
        verify(educationMapper, never()).toEducation(any());
        verify(educationRepository, never()).save(any());
        verify(educationMapper, never()).toEducationDto(any());
    }

    @Test
    void testAddEducation_InvalidYearFrom() {
        educationDto.setYearFrom(Year.now().getValue() + 1);

        assertThrows(DataValidationException.class, () -> educationService.addEducation(1L, educationDto));
    }

    @Test
    void testUpdateEducation_Success() {
        when(educationRepository.findById(1L)).thenReturn(Optional.of(education));
        when(educationMapper.toEducation(educationDto)).thenReturn(education);
        when(educationRepository.save(education)).thenReturn(education);
        when(educationMapper.toEducationDto(education)).thenReturn(educationDto);

        EducationDto result = educationService.updateEducation(1L, educationDto);

        assertNotNull(result);
        assertEquals(educationDto.getId(), result.getId());
        verify(educationRepository, times(1)).findById(1L);
        verify(educationRepository, times(1)).save(education);
    }

    @Test
    void testUpdateEducation_EducationNotFound() {
        when(educationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> {
            educationService.updateEducation(1L, educationDto);
        });

        verify(educationRepository, times(1)).findById(1L);
        verify(educationMapper, never()).toEducation(any());
        verify(educationRepository, never()).save(any());
        verify(educationMapper, never()).toEducationDto(any());
    }

    @Test
    void testUpdateEducation_UserNotOwner() {
        Education anotherUserEducation = new Education();
        anotherUserEducation.setId(1L);

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUserEducation.setUser(anotherUser);

        when(educationRepository.findById(1L)).thenReturn(Optional.of(anotherUserEducation));

        assertThrows(DataValidationException.class, () -> {
            educationService.updateEducation(1L, educationDto);
        });

        verify(educationRepository, times(1)).findById(1L);
        verify(educationMapper, never()).toEducation(any());
        verify(educationRepository, never()).save(any());
        verify(educationMapper, never()).toEducationDto(any());
    }

    @Test
    void testUpdateEducation_InvalidUserId() {
        when(educationRepository.findById(1L)).thenReturn(Optional.of(education));

        educationDto.setId(1L);
        education.setUser(new User());
        education.getUser().setId(2L);

        assertThrows(DataValidationException.class, () -> educationService.updateEducation(1L, educationDto));
    }

    @Test
    void testGetById_Success() {
        when(educationRepository.findById(1L)).thenReturn(Optional.of(education));
        when(educationMapper.toEducationDto(education)).thenReturn(educationDto);

        EducationDto result = educationService.getById(1L);

        assertNotNull(result);
        assertEquals(educationDto, result);
        verify(educationRepository, times(1)).findById(1L);
        verify(educationMapper, times(1)).toEducationDto(education);
    }

    @Test
    void testGetById_EducationNotFound() {
        when(educationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> {
            educationService.getById(1L);
        });

        verify(educationRepository, times(1)).findById(1L);
        verify(educationMapper, never()).toEducationDto(any());
    }
}