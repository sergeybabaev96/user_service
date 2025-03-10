package school.faang.user_service.service.country;

import school.faang.user_service.entity.Country;

public interface CountryService {
    Country getOrCreateCountry(String countryTitle);
}
