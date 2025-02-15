package school.faang.user_service.service.user;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.CountryService;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Spy
    private UserMapperImpl userMapper;
    @Spy
    private CsvMapper csvMapper;
    @Mock
    private CountryService countryService;
    @Mock
    private CountryRepository countryRepositoryMock;

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepositoryMock;
    @Autowired
    private ExecutorService executorService;
    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(10);
    }

    @Test
    @DisplayName("Test parse CSV")
    void parseCsv() throws IOException {
/*        ClassPathResource resource = new ClassPathResource("files/students.csv");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                resource.getFilename(),
                "text/csv",
                resource.getInputStream()
        );
        userService.processPersonsFromFile(file);
        Mockito.verify(userRepositoryMock, Mockito.times(1)).saveAll(Mockito.anyCollection());*/
    }
}