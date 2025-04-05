package school.faang.user_service.service.country;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    @Spy
    private CountryService countryService;

    private static final String NAME_OF_COUNTRY_IN_DB = "old Country";
    private static final Country COUNTRY_IN_DB = new Country(1, NAME_OF_COUNTRY_IN_DB, List.of());

    @Test
    @DisplayName("getOrCreateCountry(). Positive. Country is already in Repository.")
    void getOrCreateCountryPositive() {
        String nameOfNewCountry = NAME_OF_COUNTRY_IN_DB;

        Mockito.when(countryService.getCountry(NAME_OF_COUNTRY_IN_DB)).thenReturn(Optional.of(COUNTRY_IN_DB));

        Country returnCountry = countryService.getOrCreateCountry(nameOfNewCountry);

        Mockito.verify(countryService, Mockito.times(1)).getCountry(nameOfNewCountry);
        Mockito.verify(countryService, Mockito.never()).createCountry(nameOfNewCountry);

    }

    @Test
    @DisplayName("getOrCreateCountry(). Negative. Country is not yet in Repository.")
    void getOrCreateCountryNegative() {
        String nameOfNewCountry = "new Country";

        Mockito.when(countryService.getCountry(nameOfNewCountry)).thenReturn(Optional.empty());

        Country returnCountry = countryService.getOrCreateCountry(nameOfNewCountry);

        Mockito.verify(countryService, Mockito.times(1)).getCountry(nameOfNewCountry);
        Mockito.verify(countryService, Mockito.times(1)).createCountry(nameOfNewCountry);
    }

    @Test
    @DisplayName("getCountry(). Positive. Get Country from Repository.")
    void getCountry() {
        Mockito.when(countryRepository.findByTitle(Mockito.any())).thenReturn(Optional.of(COUNTRY_IN_DB));

        Optional<Country> optional = countryService.getCountry(NAME_OF_COUNTRY_IN_DB);

        Mockito.verify(countryRepository, Mockito.times(1)).findByTitle(Mockito.any());
    }

    @Test
    @DisplayName("createCountry(). Positive. Create new Country in Repository.")
    void createCountry() {
        Mockito.when(countryRepository.save(any(Country.class))).thenReturn(COUNTRY_IN_DB);

        Country returnCountry = countryService.createCountry(NAME_OF_COUNTRY_IN_DB);

        Mockito.verify(countryRepository, Mockito.times(1)).save(any(Country.class));
    }
}