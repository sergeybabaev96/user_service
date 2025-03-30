package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public Country findOrCreateCountry(String title) {
        return countryRepository.findByTitle(title)
                .orElseGet(() -> countryRepository.save(Country
                        .builder()
                        .title(title)
                        .build()));
    }
}
