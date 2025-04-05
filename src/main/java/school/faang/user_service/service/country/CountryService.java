package school.faang.user_service.service.country;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;


    public Country getOrCreateCountry(String country) {
        Optional<Country> countryFromRepository = getCountry(country);

        return countryFromRepository.orElseGet(() -> createCountry(country));
    }

    public Optional<Country> getCountry(String country) {
        return countryRepository.findByTitle(country);    }

    public Country createCountry(String country) {
        Country entity = new Country();
        entity.setTitle(country);
        return countryRepository.save(entity);
    }
}
