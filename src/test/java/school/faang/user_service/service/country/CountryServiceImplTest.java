package school.faang.user_service.service.country;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.CountryRepository;

@ExtendWith(MockitoExtension.class)
class CountryServiceImplTest {

    @Mock
    CountryRepository countryRepositoryMock;
    @InjectMocks
    CountryServiceImpl countryService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getOrCreateCountry() {
        String countryTitle = "Some country";
        countryService.getOrCreateCountry(countryTitle);
        Mockito.verify(countryRepositoryMock, Mockito.times(1))
                .findByTitleIgnoreCase(countryTitle);
    }
}