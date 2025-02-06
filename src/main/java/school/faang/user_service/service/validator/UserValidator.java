package school.faang.user_service.service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;

    public void validateNewUser(UserCreateDto userCreateDto) {
        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
        if (userRepository.existsByUsername(userCreateDto.getUsername())) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }
        if (!Objects.equals(userCreateDto.getPassword(), userCreateDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }
        if (!countryRepository.existsById(userCreateDto.getCountryId())) {
            throw new IllegalArgumentException("Страны с таким id "
                    + userCreateDto.getCountryId()
                    + " не существует");
        }
    }
}
