package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.EducationViewDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.education.EducationService;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EducationServiceTest {
    @InjectMocks
    private EducationService educationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EducationRepository educationRepository;

    @Spy
    private EducationMapper educationMapper = Mappers.getMapper(EducationMapper.class);

    private User user = new User();
    private User anotherUser;
    private Education education = new Education();
    private EducationViewDto educationViewDto = new EducationViewDto();

    @BeforeEach
    void setUp() {
        user.setId(1L);
        user.setEducation(List.of(education));
        education.setUser(user);
        educationViewDto.setId(10L);
        educationViewDto.setYearFrom(Year.now().getValue());
        educationViewDto.setYearTo(Year.now().getValue() + 4);
        educationViewDto.setInstitution("");
        educationViewDto.setEducationLevel("");
        educationViewDto.setSpecialization("");

        anotherUser = new User();
        anotherUser.setId(2L);
    }

    @DisplayName("Проверка получения ошибки при указании года начала обучения больше текущего")
    @Test
    void testAddEducationWithIncorrectYearFrom() {
        EducationViewDto educationDto = new EducationViewDto();
        educationDto.setYearFrom(Year.now().getValue() + 1);
        long userId = 3L;

        Assertions.assertThrows(DataValidationException.class, () -> educationService.addEducation(userId, educationDto));
    }

    @DisplayName("Проверка получения ошибки при попытке найти пользователя по несуществующему id")
    @Test
    void testAddEducationWithNonExistingUserId() {
        EducationViewDto educationDto = new EducationViewDto();
        educationDto.setYearFrom(Year.now().getValue());
        long userId = 5L;
        Assertions.assertThrows(DataValidationException.class, () -> educationService.addEducation(userId, educationDto));
    }

    @DisplayName("Проверка успешного добавления данных об образовании")
    @Test
    void testAddEducation() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(educationMapper.toEducation(educationViewDto)).thenReturn(education);
        Mockito.when(educationRepository.save(education)).thenReturn(education);
        Mockito.when(educationMapper.toEducationDto(education)).thenReturn(educationViewDto);
        EducationViewDto result = educationService.addEducation(user.getId(), educationViewDto);
        Assertions.assertEquals(educationViewDto, result);
    }



    @DisplayName("Проверка получения ошибки при попытке найти данные об образовании по несуществующему id")
    @Test
    void testUpdateEducationWithNonExistingEducationId() {

    }

    @DisplayName("Проверка получения ошибки при попытке обновить данные об образовании третьим лицом")
    @Test
    void testUpdateEducationWithNotSuccessfulValidateUser() {

    }


    @DisplayName("Проверка успешного обновления данных об образовании")
    @Test
    void testUpdateEducation() {
        Mockito.when(educationRepository.findById(10L)).thenReturn(Optional.of(education));
        Mockito.when(educationMapper.toEducationDto(education)).thenReturn(educationViewDto);
        Mockito.when(educationMapper.toEducation(educationViewDto)).thenReturn(education);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(educationRepository.save(education)).thenReturn(education);
        Mockito.when(educationMapper.toEducationDto(education)).thenReturn(educationViewDto);
        EducationViewDto result = educationService.updateEducation(user.getId(), educationViewDto);

        Assertions.assertEquals(educationViewDto, result);
    }

}
