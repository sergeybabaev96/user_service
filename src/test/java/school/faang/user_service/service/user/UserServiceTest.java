package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private final long id = 1;

    @InjectMocks
    private UserService userService;

    @Spy
    private CsvUserMapperImpl csvUserMapper;

    @Spy
    private CsvMapper csvMapper = new CsvMapper();


    @Mock
    private EducationRepository educationRepository;

    @Mock
    private Validator validator;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private UserRepository userRepository;

    private final long userId = 1L;
    private final User user = User.builder().id(userId).build();

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

    @Test
    public void save_shouldSave() {
        userService.save(user);

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    public void findById_shouldFind() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(userId);

        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }
    private boolean getResult(Optional<User> foundUser) {
        when(userRepository.findById(userId)).thenReturn(foundUser);
        return userService.isWithinGoalLimit(userId);
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

        when(validator.validate(any())).thenReturn(Collections.emptySet());
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.existsByPhone(dto.getPhone())).thenReturn(false);
        when(csvUserMapper.toUser(any())).thenReturn(user);
        when(csvUserMapper.toEducation(any())).thenReturn(education);
        when(countryRepository.findByTitleIgnoreCase("USA")).thenReturn(Optional.of(country));

        userService.processCsv(mockFile);

        verify(userRepository, times(1)).save(user);
        verify(educationRepository, times(1)).save(education);
        verify(csvUserMapper, times(1)).toUser(any());
        verify(csvUserMapper, times(1)).toEducation(any());
        verify(validator, times(1)).validate(any());
    }


    @Test
    public void shouldSkipInvalidUsersWhenValidationFails() throws Exception {
        String csvContent = "email,phone,country,firstName,lastName\n" +
                "invalid@example.com,1234567890,USA,Invalid,User";

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "users.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        Set<ConstraintViolation<CsvUserDto>> violations = Set.of(mock(ConstraintViolation.class));
        when(validator.validate(any(CsvUserDto.class))).thenReturn(violations);


        userService.processCsv(mockFile);

        verify(userRepository, never()).save(any());
        verify(educationRepository, never()).save(any());
    }

    @Test
    public void shouldSkipDuplicateUsersWhenEmailOrPhoneExists() throws Exception {
        String csvContent = "email,phone,country,firstName,lastName\n" +
                "x@y.com,123,USA,X,Y";

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "users.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        when(validator.validate(any())).thenReturn(Collections.emptySet());
        when(userRepository.existsByEmail("x@y.com")).thenReturn(true);
        when(userRepository.existsByPhone("123")).thenReturn(false);

        userService.processCsv(mockFile);

        verify(userRepository, never()).save(any());
        verify(educationRepository, never()).save(any());
    }


    @Test
    public void shouldThrowIOExceptionWhenFileReadingFails() throws Exception {
        MultipartFile brokenFile = mock(MultipartFile.class);
        when(brokenFile.getInputStream()).thenThrow(new IOException("File read error"));

        assertThrows(IOException.class, () -> userService.processCsv(brokenFile));

        verify(userRepository, never()).save(any());
        verify(educationRepository, never()).save(any());
    }

}