package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import school.faang.user_service.csv.model.person.Address;
import school.faang.user_service.csv.model.person.ContactInfo;
import school.faang.user_service.csv.model.person.Person;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.csv.PersonCsvDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.PersonCsvMapper;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.parser.CsvParserService;
import school.faang.user_service.validator.user.UserValidator;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    public static final String USER_NOT_FOUND = "User not found";
    private static final Long USER_ID = 1L;

    private User user;
    private UserDto userDto;
    private List<User> users;
    private List<UserDto> userDtos;
    private List<Long> userIds;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private PersonMapper personMapper;

    @Mock
    private CsvParserService csvParserService;

    @Mock
    private PersonCsvMapper personCsvMapper;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private MockMultipartFile file;

    @BeforeEach
    public void setUp() {
        user = User.builder().id(USER_ID).username("Join").build();
        userDto = UserDto.builder().id(USER_ID).username("Join").build();
        users = List.of(user, User.builder().id(2L).username("Bob").build());
        userDtos = List.of(userDto, UserDto.builder().id(2L).username("Bob").build());
        userIds = List.of(1L, 2L);

        file = new MockMultipartFile(
                "file",
                "users.csv",
                "text/csv",
                "firstName,lastName,email,phone\nJohn,Doe,john@example.com,1234567890".getBytes()
        );
    }

    @Test
    public void testGetUserNotFound() {
        when(userRepository.findById(USER_ID))
                .thenThrow(new EntityNotFoundException(USER_NOT_FOUND));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.getUser(USER_ID));
        assertEquals(USER_NOT_FOUND, exception.getMessage());
        verify(userRepository, times(1)).findById(USER_ID);
        verify(userMapper, never()).toUser(any());
    }

    @Test
    public void testGetUserSuccessful() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        UserDto resultDto = userService.getUser(USER_ID);

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userMapper, times(1)).toUser(user);
        assertNotNull(resultDto);
        assertEquals(userDto.getId(), resultDto.getId());
        assertEquals(userDto.getUsername(), resultDto.getUsername());
    }

    @Test
    public void testGetUsersByIdsSuccessful() {
        when(userRepository.findAllById(userIds)).thenReturn(users);

        List<UserDto> resultDtos = userService.getUsersByIds(userIds);

        assertNotNull(resultDtos);
        assertEquals(userDtos.size(), resultDtos.size());
        assertEquals(userDtos.get(0).getId(), resultDtos.get(0).getId());
        assertEquals(userDtos.get(0).getUsername(), resultDtos.get(0).getUsername());
        assertEquals(userDtos.get(1).getId(), resultDtos.get(1).getId());
        assertEquals(userDtos.get(1).getUsername(), resultDtos.get(1).getUsername());
        verify(userRepository, times(1)).findAllById(userIds);
        verify(userMapper, times(1)).toListUserDto(users);
    }

    @Test
    void testUploadUsersFromCsvSuccessfullyCreatesUser() {
        PersonCsvDto dto = new PersonCsvDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john@example.com");
        dto.setPhone("1234567890");

        when(csvParserService.parseCsv(file)).thenReturn(List.of(dto));
        when(userRepository.existsByUsername("JohnDoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.existsByPhone("1234567890")).thenReturn(false);

        User user = new User();
        Country country = new Country();
        Person person = mock(Person.class);
        ContactInfo contactInfo = new ContactInfo();
        Address address = new Address();
        address.setCountry("USA");
        contactInfo.setAddress(address);

        when(person.getContactInfo()).thenReturn(contactInfo);
        when(personCsvMapper.toPerson(dto)).thenReturn(person);
        when(countryRepository.findAll()).thenReturn(Collections.emptyList());
        when(countryRepository.save(any())).thenReturn(country);
        when(personMapper.toUser(person, country)).thenReturn(user);

        Map<String, String> result = userService.uploadUsersFromCsv(file);

        assertEquals(1, result.size());
        assertEquals("created success", result.get("JohnDoe"));
        verify(userRepository).save(userCaptor.capture());
        assertEquals(user, userCaptor.getValue());
    }

    @Test
    void testUploadUsersFromCsvSkipsDuplicateUser() {
        PersonCsvDto dto = new PersonCsvDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");

        when(csvParserService.parseCsv(file)).thenReturn(List.of(dto));
        when(userRepository.existsByUsername("JohnDoe")).thenReturn(true);

        Map<String, String> result = userService.uploadUsersFromCsv(file);

        assertEquals(1, result.size());
        assertEquals("user name JohnDoe already exist", result.get("JohnDoe"));
        verify(userRepository, never()).save(any());
    }
}
