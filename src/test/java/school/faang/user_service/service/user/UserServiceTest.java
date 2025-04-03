package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import school.faang.user_service.dto.csv.CsvUserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.csv.CsvUserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.csv.CsvParsingService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private final long id = 1;

    @InjectMocks
    private UserService userService;

    @Mock
    private CsvUserMapper csvUserMapper;

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private CsvParsingService csvParsingService;

    @Mock
    private Validator validator;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testIsWithinGoalLimit() {
        Optional<User> foundUser = Optional.of(User.builder()
                .goals(List.of(
                        Goal.builder().build(),
                        Goal.builder().build()))
                .build());

        boolean answer = getResult(foundUser);

        verify(userRepository, times(1)).findById(id);
        assertTrue(answer);
    }

    @Test
    public void testIsWithinOverGoalLimit() {
        Optional<User> foundUser = Optional.of(User.builder()
                .goals(List.of(
                        Goal.builder().build(),
                        Goal.builder().build(),
                        Goal.builder().build(),
                        Goal.builder().build()))
                .build());

        boolean result = getResult(foundUser);

        verify(userRepository, times(1)).findById(id);
        assertFalse(result);
    }

    @Test
    public void testIsWithinGoalLimitWithNotFoundUser() {
        assertThrows(EntityNotFoundException.class, () -> getResult(Optional.empty()));
    }

    private boolean getResult(Optional<User> foundUser) {
        when(userRepository.findById(id)).thenReturn(foundUser);
        return userService.isWithinGoalLimit(id);
    }

    @Test
    public void shouldParseAllUsersFromCsvFileWhenFileIsValid() throws IOException {
        String csvContent = "email,phone,country,firstName,lastName\n" +
                "test@example.com,1234567890,USA,John,Doe";

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "users.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        CsvUserDto dto = new CsvUserDto();
        dto.setEmail("test@example.com");
        dto.setPhone("1234567890");
        dto.setCountry("USA");
        dto.setFirstName("John");
        dto.setLastName("Doe");

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUsername("johndoe");

        Education education = new Education();
        Country country = new Country();
        country.setTitle("USA");

        when(csvParsingService.parseUsers(mockFile)).thenReturn(List.of(dto));
        when(validator.validate(dto)).thenReturn(Collections.emptySet());
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.existsByPhone(dto.getPhone())).thenReturn(false);
        when(csvUserMapper.toUser(dto)).thenReturn(user);
        when(csvUserMapper.toEducation(dto)).thenReturn(education);
        when(countryRepository.findByTitleIgnoreCase("USA")).thenReturn(Optional.of(country));

        userService.processCsv(mockFile);

        verify(userRepository, times(1)).save(user);
        verify(educationRepository, times(1)).save(education);
        verify(csvUserMapper, times(1)).toUser(dto);
        verify(csvUserMapper, times(1)).toEducation(dto);
        verify(csvParsingService, times(1)).parseUsers(mockFile);
        verify(validator, times(1)).validate(dto);
    }

    @Test
    public void shouldSkipInvalidUsersWhenValidationFails() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "users.csv", "text/csv", "invalid,data".getBytes());

        CsvUserDto invalidDto = new CsvUserDto();
        invalidDto.setEmail("invalid@example.com");

        when(csvParsingService.parseUsers(mockFile)).thenReturn(List.of(invalidDto));

        @SuppressWarnings("unchecked")
        Set<ConstraintViolation<CsvUserDto>> violations = Set.of(mock(ConstraintViolation.class));
        when(validator.validate(invalidDto)).thenReturn(violations);

        userService.processCsv(mockFile);

        verify(userRepository, never()).save(any());
        verify(educationRepository, never()).save(any());
    }

    @Test
    public void shouldSkipDuplicateUsersWhenEmailOrPhoneExists() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "users.csv", "text/csv", "email,phone,country\nx@y.com,123,USA".getBytes());

        CsvUserDto dto = new CsvUserDto();
        dto.setEmail("x@y.com");
        dto.setPhone("123");

        when(csvParsingService.parseUsers(mockFile)).thenReturn(List.of(dto));
        when(validator.validate(dto)).thenReturn(Collections.emptySet());
        when(userRepository.existsByEmail("x@y.com")).thenReturn(true);
        when(userRepository.existsByPhone("123")).thenReturn(false);

        userService.processCsv(mockFile);

        verify(userRepository, never()).save(any());
        verify(educationRepository, never()).save(any());
    }

    @Test
    public void shouldThrowIOExceptionWhenCsvParsingFails() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "users.csv", "text/csv", new byte[0]);

        when(csvParsingService.parseUsers(mockFile)).thenThrow(new IOException("Parse failed"));

        assertThrows(IOException.class, () -> userService.processCsv(mockFile));

        verify(userRepository, never()).save(any());
        verify(educationRepository, never()).save(any());
    }
}
