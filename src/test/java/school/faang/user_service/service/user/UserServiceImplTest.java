package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import school.faang.user_service.dto.file.FileUploadResponseDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.CountryService;

import java.io.IOException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private CountryService countryService;
    @Mock
    private CountryRepository countryRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @BeforeEach
    void setUp() {
    }

    @Test
    void parseCsv() throws IOException {
        ClassPathResource resource = new ClassPathResource("files/students.csv");
        Country testCountry = Country.builder().title("USA").build();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                resource.getFilename(),
                "text/csv",
                resource.getInputStream()
        );
        Mockito.when(countryRepository.findByTitleIgnoreCase(Mockito.anyString()))
                .thenReturn(Optional.ofNullable(testCountry));
        //Mockito.when(countryService.getOrCreateCountry(Mockito.any()))
                //.thenReturn(testCountry);
        Mockito.when(userRepository.save(Mockito.any(Mockito.any())))
                .thenReturn(User.builder().username("test").build());
        FileUploadResponseDto fileUploadResponseDto = userService.parseCsv(file.getInputStream());
    }
}