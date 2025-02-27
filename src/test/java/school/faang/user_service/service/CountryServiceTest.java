package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;
    private CountryService countryService;

    @BeforeEach
    void setUp() {
        countryService = new CountryService(countryRepository);
    }

    @Test
    void findOrCreateCountry_WhenCountryExists() {
        String countryTitle = "USA";
        Country country = Country.builder().id(1L).title(countryTitle).build();
        when(countryRepository.findByTitle(countryTitle)).thenReturn(Optional.of(country));
        Country result = countryService.findOrCreateCountry(countryTitle);
        assertEquals(country, result);
        verify(countryRepository, times(1)).findByTitle(countryTitle);
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    void findOrCreateCountry_WhenCountryDoesNotExist() {
        String countryTitle = "USA";
        Country country = Country.builder().id(1L).title("USA").build();
        when(countryRepository.findByTitle(countryTitle)).thenReturn(Optional.empty());
        when(countryRepository.save(any(Country.class))).thenReturn(country);
        Country result = countryService.findOrCreateCountry(countryTitle);
        assertEquals(country, result);
        verify(countryRepository, times(1)).findByTitle(countryTitle);
        verify(countryRepository, times(1)).save(any(Country.class));
    }
}
