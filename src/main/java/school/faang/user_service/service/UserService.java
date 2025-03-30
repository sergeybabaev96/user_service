package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.preson.PersonAboutDto;
import school.faang.user_service.dto.preson.PersonContactDto;
import school.faang.user_service.dto.preson.PersonDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.CsvMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final CsvMapper csvMapper;
    private final UserMapper userMapper;
    private final CountryRepository countryRepository;
    private final PasswordService passwordService;
    private static final int PASSWORD_LENGTH = 10;
    private static final int MIN_DATA_LENGTH = 24;

    public List<UserDto> registerUserFromFile(MultipartFile file) {
        List<String> validatedFile = validateAndReadFile(file);
        List<CompletableFuture<UserDto>> futures = new ArrayList<>();

        for (String line : validatedFile) {
            CompletableFuture<UserDto> future = CompletableFuture.supplyAsync(() -> registerUserFromLine(line));
            futures.add(future);
        }

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    private UserDto registerUserFromLine(String line) {
        String[] fields = line.split(",");
        if (fields.length < MIN_DATA_LENGTH) {
            throw new DataValidationException("Некорректный формат данных в файле");
        }

        PersonDto personDto = parsePersonDto(fields);
        PersonContactDto personContactDto = parsePersonContactDto(fields);
        PersonAboutDto personAboutDto = parsePersonAboutDto(fields);

        User user = csvMapper.toUser(personDto, personContactDto, personAboutDto);
        user.setPassword(passwordService.generateRandomPassword(PASSWORD_LENGTH));
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(()
                -> new RuntimeException("User with id " + id + " not found"));
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);

        if (users.isEmpty()) {
            return Collections.emptyList();
        } else {
            return users.stream()
                    .map(userMapper::toDto)
                    .toList();
        }
    }

    private List<String> validateAndReadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new DataValidationException("Файл пуст");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            return reader.lines().toList();
        } catch (IOException e) {
            throw new DataValidationException("Ошибка чтения файла");
        }
    }

    private PersonDto parsePersonDto(String[] fields) {
        PersonDto personDto = new PersonDto();
        personDto.setFirstName(fields[0]);
        personDto.setLastName(fields[1]);
        return personDto;
    }

    private PersonContactDto parsePersonContactDto(String[] fields) {
        PersonContactDto personContactDto = new PersonContactDto();

        personContactDto.setEmail(fields[5]);
        personContactDto.setPhone(fields[6]);
        personContactDto.setCity(fields[8]);
        String countryTitle = fields[10];
        Country country = countryRepository.findByTitle(countryTitle)
                .orElseGet(() -> {
                    Country newCountry = new Country();
                    newCountry.setTitle(countryTitle);
                    return countryRepository.save(newCountry);
                });
        personContactDto.setCountry(country);
        return personContactDto;
    }

    private PersonAboutDto parsePersonAboutDto(String[] fields) {
        PersonAboutDto personAboutDto = new PersonAboutDto();
        personAboutDto.setFaculty(fields[12]);
        personAboutDto.setYearOfStudy(fields[13]);
        personAboutDto.setMajor(fields[14]);
        personAboutDto.setEmployer(fields[23]);
        return personAboutDto;
    }
}
