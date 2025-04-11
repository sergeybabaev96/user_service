package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.CreateUserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class CreateUserValidator {

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;

    public void validateUsername(CreateUserDto userDto) {
        if (userRepository.existsByUsername(userDto.username())) {
            throw new DataValidationException("User '%s' is already existed".formatted(userDto.username()));
        }
    }

    public void validateUserEmail(CreateUserDto userDto) {
        if (userRepository.existsByEmail(userDto.email())) {
            throw new DataValidationException(
                    "User with email '%s' is already existed".formatted(userDto.email()));
        }
    }

    public Country validateCountryTitle(CreateUserDto userDto) {
        return countryRepository.findByTitle(userDto.countryTitle())
                .orElseThrow(() -> new DataValidationException(
                        "User with email '%s' is already existed".formatted(userDto.email())));
    }
}
