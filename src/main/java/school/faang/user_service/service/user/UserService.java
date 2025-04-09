package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.csv.CsvUserDto;
import school.faang.user_service.entity.*;
import school.faang.user_service.mapper.csv.CsvUserMapper;
import school.faang.user_service.repository.*;

import java.io.IOException;

import java.io.InputStream;
import java.util.*;

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

    @Transactional
    public void processCsv(MultipartFile file) throws IOException {

        List<CsvUserDto> users = parseUsers(file);
        log.info("📄 Total rows parsed from CSV: {}", users.size());

        List<CsvUserDto> validUsers = getValidUsers(users);
        log.info("✅ Valid users to save: {}", validUsers.size());
        validUsers.forEach(this::saveUserWithEducation);
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
        boolean emailExists = userRepository.existsByEmail(dto.getEmail());
        boolean phoneExists = userRepository.existsByPhone(dto.getPhone());

        if (emailExists || phoneExists) {
            log.warn("⛔ Duplicate: email = {}, phone = {}", dto.getEmail(), dto.getPhone());
        }

        return emailExists || phoneExists;
    }

    private List<CsvUserDto> parseUsers(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            MappingIterator<CsvUserDto> it = csvMapper
                    .readerFor(CsvUserDto.class)
                    .with(schema)
                    .readValues(inputStream);
            return it.readAll();

        }
    }

    private List<CsvUserDto> getValidUsers(List<CsvUserDto> users) {
        List<CsvUserDto> validUsers = new ArrayList<>();
        for (CsvUserDto dto : users) {
            Set<ConstraintViolation<CsvUserDto>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                log.warn("x Validation failed for user: {}", dto.getEmail());
                violations.forEach(v -> log.warn("  {} - {}", v.getPropertyPath(), v.getMessage()));
                continue;
            }

            if (isDuplicate(dto)) {
                log.warn("⛔ Duplicate found for user: {}", dto.getEmail());
                continue;
            }

            validUsers.add(dto);
        }
        return validUsers;
    }

    private void saveUserWithEducation(CsvUserDto dto){
        User user = csvUserMapper.toUser(dto);
        user.setPassword(generatePassword());
        user.setCountry(getOrCreateCountry(dto.getCountry()));
        user.setActive(true);

        log.info("💾 Saving user: {} ({})", user.getUsername(), user.getEmail());
        userRepository.save(user);

        Education education = csvUserMapper.toEducation(dto);
        education.setUser(user);
        educationRepository.save(education);
        log.info("📚 Education and previous education saved for: {}", user.getUsername());
    }

}
