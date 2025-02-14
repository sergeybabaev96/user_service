package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.filter.UserFilter;
import school.faang.user_service.service.filter.realisation.CityFilter;
import school.faang.user_service.service.goal.GoalService;

import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalService goalService;
    @Mock
    private EventService eventService;
    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private List<UserFilter> userFilters;
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private CountryService countryService;

    @InjectMocks
    private UserService service;

    private User user;
    private UserDto userDto;

    private User premiumUser1;
    private User premiumUser2;

    private Premium premium1;
    private Premium premium2;

    @BeforeEach
    public void init() {
        user = User.builder()
                .id(1L)
                .username("Bob")
                .active(true)
                .email("bob@example.com")
                .goals(List.of(
                        Goal.builder().id(1L).build(),
                        Goal.builder().id(2L).build(),
                        Goal.builder().id(3L).build(),
                        Goal.builder().id(4L).build()
                ))
                .ownedEvents(List.of(
                        Event.builder().id(1L).startDate(LocalDateTime.now().plusDays(10)).build(),
                        Event.builder().id(2L).startDate(LocalDateTime.now().plusDays(10)).build(),
                        Event.builder().id(3L).startDate(LocalDateTime.now().plusDays(10)).build()
                ))
                .build();

        premium1 = Premium.builder()
                .id(1L)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build();

        premium2 = Premium.builder()
                .id(2L)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build();

        premiumUser1 = User.builder()
                .id(2L)
                .username("Alice")
                .active(true)
                .city("Tashkent")
                .premium(premium1)
                .build();

        premiumUser2 = User.builder()
                .id(3L)
                .username("Charlie")
                .active(true)
                .city("Tashkent")
                .premium(premium2)
                .build();

        userDto = new UserDto(1L, "Bob", "bob@example.com");
    }

    @Test
    public void deactivateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service.deactivateUser(1L);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(savedUser.getId(), user.getId());
        assertTrue(savedUser.getOwnedEvents().isEmpty());
    }

    @Test
    public void getAllPremiumUserSuccess() {
        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(premiumUser1, premiumUser2));
        when(userFilters.stream()).thenReturn(Stream.of(new CityFilter()));

        UserFilterDto userFilterDto = UserFilterDto.builder().cityPattern("Tashkent").build();

        List<UserDto> result = service.getPremiumUsers(userFilterDto);

        assertEquals(2, result.size());
        assertEquals(premiumUser1.getUsername(), result.get(0).username());
    }

    @Test
    public void getUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = service.getUser(1L);
        assertEquals(userDto, result);
    }

    @Test
    public void getUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> service.getUser(1L));
        assertEquals("User with id 1 not found", exception.getMessage());
    }

    @Test
    public void getUsersByIds_Success() {
        List<Long> ids = List.of(1L, 2L);
        User user2 = User.builder().id(2L).username("Bob").build();

        when(userRepository.findAllById(ids)).thenReturn(List.of(user, user2));

        List<UserDto> result = service.getUsersByIds(ids);
        assertEquals(2, result.size());
    }

    @Test
    public void processCsvFile_WithValidCsvFromResources_SavesAllUsers() throws Exception {
        InputStream csvStream = getClass().getClassLoader().getResourceAsStream("files/students.csv");
        MockMultipartFile csvFile = new MockMultipartFile("file", "students.csv", "text/csv", csvStream);
        Country country = Country.builder().id(1L).title("USA").build();
        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(countryService.findOrCreateCountry(anyString())).thenReturn(country);
        when(userMapper.toEntity(any(), anyString(), anyString(), any(), anyString())).thenReturn(new User());
        service.processCsvFile(csvFile);
        verify(userRepository).saveAll(argThat(iterator -> {
            List<User> users = new ArrayList<>();
            iterator.forEach(users::add);
            return users.size() == 4;
        }));
    }

    @Test
    public void processCsvFile_EmptyCsvFile_ThrowsUncheckedIOException() {
        MockMultipartFile csvFile =
                new MockMultipartFile("file", "empty.csv", "text/csv", new byte[0]);
        assertThrows(UncheckedIOException.class, () -> service.processCsvFile(csvFile));
    }

    @Test
    public void processCsvFile_WithExistingUsername_GeneratesNewUsername() throws Exception {
        InputStream csvStream = getClass().getClassLoader().getResourceAsStream("files/students.csv");
        MockMultipartFile csvFile = new MockMultipartFile("file", "students.csv", "text/csv", csvStream);
        Country country = Country.builder().id(1L).title("USA").build();
        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername("John.Doe")).thenReturn(true).thenReturn(false);
        when(countryService.findOrCreateCountry(anyString())).thenReturn(country);
        when(userMapper.toEntity(any(), anyString(), anyString(), any(), anyString())).thenAnswer(invocation -> {
            User user1 = new User();
            user1.setUsername(invocation.getArgument(1));
            return user1;
        });
        service.processCsvFile(csvFile);
        verify(userRepository).saveAll(argThat(iterator -> {
            List<User> users = new ArrayList<>();
            iterator.forEach(users::add);
            return users.stream().anyMatch(user1 -> user1.getUsername() != null
                    && user1.getUsername().matches("John.Doe\\d+"));
        }));
    }
}
