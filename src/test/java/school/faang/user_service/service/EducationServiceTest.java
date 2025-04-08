package school.faang.user_service.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.education.EducationService;

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


    @Test
    @DisplayName("The test checks whether the formation has been added correctly")
    void addEducationShouldAddEducationWhenValidInput() {
        long userId = 1L;
        EducationDto inputDto = new EducationDto();
        inputDto.setYearFrom(Year.now().getValue() - 1);

        User user = new User();
        user.setId(userId);
        user.setEducation(new java.util.ArrayList<>());

        Education education = new Education();
        education.setUser(user);

        EducationDto resultDto = new EducationDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(educationMapper.toEducation(inputDto)).thenReturn(education);
        when(educationRepository.save(education)).thenReturn(education);
        when(educationMapper.toEducationDto(education)).thenReturn(resultDto);

        EducationDto actual = educationService.addEducation(userId, inputDto);

        assertEquals(resultDto, actual);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Throws an exception when the year is invalid")
    void addEducationShouldThrowExceptionWhenYearFromInvalid() {
        EducationDto dto = new EducationDto();
        dto.setYearFrom(Year.now().getValue());

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> educationService.addEducation(1L, dto)
        );

        assertEquals("YearFrom must be less than the current year", exception.getMessage());
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("The test will check that the education is up to date")
    void updateEducationShouldUpdateWhenValid() {
        long userId = 1L;
        long educationId = 10L;

        EducationDto dto = new EducationDto();
        dto.setId(educationId);
        dto.setYearFrom(Year.now().getValue() - 2);

        User user = new User();
        user.setId(userId);

        Education education = new Education();
        education.setId(educationId);
        education.setUser(user);

        when(educationRepository.findById(educationId)).thenReturn(Optional.of(education));
        when(educationRepository.save(any())).thenReturn(education);
        when(educationMapper.toEducationDto(education)).thenReturn(dto);

        EducationDto updated = educationService.updateEducation(userId, dto);

        assertEquals(dto, updated);
        verify(educationMapper).updateEducationFromDto(dto, education);
    }

    @Test
    @DisplayName("Throws an exception when the year is invalid")
    void updateEducationShouldThrowWhenYearFromInvalid() {
        EducationDto dto = new EducationDto();
        dto.setYearFrom(Year.now().getValue());

        assertThrows(DataValidationException.class, () -> educationService.updateEducation(1L, dto));
        verify(educationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Throws an exception when the user is invalid")
    void updateEducationShouldThrowWhenWrongUser() {
        EducationDto dto = new EducationDto();
        dto.setId(100L);
        dto.setYearFrom(Year.now().getValue() - 1);

        User anotherUser = new User();
        anotherUser.setId(99L);

        Education education = new Education();
        education.setId(100L);
        education.setUser(anotherUser);

        when(educationRepository.findById(100L)).thenReturn(Optional.of(education));

        assertThrows(DataValidationException.class, () -> educationService.updateEducation(1L, dto));
    }

    @Test
    @DisplayName("The test checks education by identifier")
    void getEducationByIdShouldReturnEducationWhenExists() {
        Education education = new Education();
        education.setId(5L);

        when(educationRepository.findById(5L)).thenReturn(Optional.of(education));

        Education found = educationService.getEducationById(5L);

        assertEquals(education, found);
    }

    @Test
    @DisplayName("The test checks that an exception is thrown if the formation by the identifier is not found")
    void getEducationByIdShouldThrowWhenNotFound() {
        when(educationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> educationService.getEducationById(99L));
    }

    @Test
    @DisplayName("Should throw DataValidationException when user not found")
    void addEducationShouldThrowExceptionWhenUserNotFound() {
        long userId = 1L;
        EducationDto inputDto = new EducationDto();
        inputDto.setYearFrom(Year.now().getValue() - 1);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> educationService.addEducation(userId, inputDto));
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository, educationMapper, educationRepository);
    }

    @Test
    @DisplayName("Should throw DataValidationException when yearFrom is in the future")
    void addEducationShouldThrowExceptionWhenYearFromInFuture() {
        long userId = 1L;
        EducationDto inputDto = new EducationDto();
        inputDto.setYearFrom(Year.now().getValue() + 1); // будущий год

        assertThrows(DataValidationException.class, () -> educationService.addEducation(userId, inputDto));
        verifyNoInteractions(userRepository, educationMapper, educationRepository);
    }
}
