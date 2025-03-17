package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.education.EducationService;

import java.util.Optional;

public class EducationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private EducationRepository educationRepository;
    @Spy
    private EducationMapper educationMapper = Mappers.getMapper(EducationMapper.class);

    @InjectMocks
    private EducationService educationService;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
    }

    @DisplayName("Проверка получения ошибки при указании года начала обучения больше текущего")
    @Test
    void testAddEducationWithIncorrectYearFrom() {

    }

    @DisplayName("Проверка успешного прохождения валидации года начала обучения")
    @Test
    void testAddEducationWithCorrectYearFrom() {

    }

    @DisplayName("Проверка получения ошибки при попытке найти пользователя по несуществующему id")
    @Test
    void testAddEducationWithNonExistingUserId() {

    }

    @DisplayName("Проверка успешного нахождения пользователя по существующему id")
    @Test
    void testAddEducationWithExistingUserId() {

    }

    @DisplayName("Проверка успешного добавления данных об образовании")
    @Test
    void testAddEducation() {

    }

    @DisplayName("Проверка получения ошибки при попытке найти данные об образовании по несуществующему id")
    @Test
    void testUpdateEducationWithNonExistingEducationId() {

    }

    @DisplayName("Проверка успешного нахождения данных об образовании по существующему id")
    @Test
    void testUpdateEducationWithExistingEducationId() {

    }

    @DisplayName("Проверка получения ошибки при попытке обновить данные об образовании третьим лицом")
    @Test
    void testUpdateEducationWithNotSuccessfulValidateUser() {

    }

    @DisplayName("Успешное прохождение проверки на попытку обновления данных об образовании третьим лицом")
    @Test
    void testUpdateEducationWithSuccessfulValidateUser() {

    }

    @DisplayName("Проверка успешного обновления данных об образовании")
    @Test
    void testUpdateEducation() {

    }

}
