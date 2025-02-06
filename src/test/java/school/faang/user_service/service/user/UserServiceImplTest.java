package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.CountryService;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private CountryService countryService;
    @Mock
    private CountryRepository countryRepositoryMock;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepositoryMock;
    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Test parse CSV")
    void parseCsv() throws IOException {
        //TODO пока не работает
        /*        ClassPathResource resource = new ClassPathResource("files/students.csv");
        Country testCountry = Country.builder().title("USA").build();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                resource.getFilename(),
                "text/csv",
                resource.getInputStream()
        );
        User user = User.builder().username("test").build();
        Mockito.when(userRepositoryMock.save(user))
                .thenReturn(user);
        Optional<Country> optionalCountry = Optional.ofNullable(testCountry);
        Mockito.when(countryRepositoryMock.findByTitleIgnoreCase(Mockito.anyString()))
                .thenReturn(optionalCountry);
        //Mockito.when(countryService.getOrCreateCountry(Mockito.any()))
                //.thenReturn(testCountry);
        FileUploadResponseDto fileUploadResponseDto = userService.parseCsv(file.getInputStream());*/
    }
}