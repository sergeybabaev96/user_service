package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.csv.CsvUserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.PreviousEducation;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.csv.CsvUserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.PreviousEducationRepository;
import school.faang.user_service.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;


import java.util.Random;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private static final int GOALS_PER_USER = 3;

    private final UserRepository userRepository;
    private final CsvMapper csvMapper;
    private final CsvUserMapper csvUserMapper;
    private final EducationRepository educationRepository;
    private final CountryRepository countryRepository;
    private final PreviousEducationRepository previousEducationRepository;
    private final Validator validator;


    public boolean isWithinGoalLimit(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        return user.getGoals().size() < GOALS_PER_USER;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    public void processCsv(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        MappingIterator<CsvUserDto> it = csvMapper
                .readerFor(CsvUserDto.class)
                .with(schema)
                .readValues(inputStream);
        List<CsvUserDto> users = it.readAll();

        users.forEach(userDto -> {
            Set<ConstraintViolation<CsvUserDto>> violations = validator.validate(userDto);
            if (!violations.isEmpty()) {
                log.warn("UserDto validation failed:");
                violations.forEach(v -> {
                    log.warn("  {} - {}", v.getPropertyPath(), v.getMessage());
                    return;
                });
            }
            if(isDuplicate(userDto)){
                return;
            }
        });

        users.forEach(userDto -> {

            User user = csvUserMapper.toUser(userDto);
            user.setPassword(generatePassword());
            user.setCountry(getOrCreateCountry(userDto.getCountry()));
            user.setActive(true);
            userRepository.save(user);
            log.info("User saved: {}", user.getUsername());

            Education education = csvUserMapper.toEducation(userDto);
            education.setUser(user);
            educationRepository.save(education);

            PreviousEducation previousEducation = csvUserMapper.toPreviousEducation(userDto);
            previousEducation.setUser(user);
            previousEducationRepository.save(previousEducation);
        });
    }


    private String generatePassword() {
        int length = 12;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&";
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    private Country getOrCreateCountry(String countryName) {
        return countryRepository.findByTitleIgnoreCase(countryName).orElseGet(() -> {
            Country newCountry = new Country();
            newCountry.setTitle(countryName);
            return countryRepository.save(newCountry);
        });
    }

    private boolean isDuplicate(CsvUserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("Email already exists {}", dto.getEmail());
            return true;
        }
        if (userRepository.existsByPhone(dto.getPhone())) {
            log.warn("Phone already exists {}", dto.getPhone());
            return true;
        }
        return false;
    }
}