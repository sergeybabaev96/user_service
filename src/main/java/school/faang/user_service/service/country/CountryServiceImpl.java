package school.faang.user_service.service.country;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.service.CountryService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;

    @Override
    public Country getOrCreateCountry(String countryTitle) {
        return countryRepository.findByTitleIgnoreCase(countryTitle)
                .orElseGet(() -> countryRepository.save(Country.builder().title(countryTitle).build()));
    }
}
