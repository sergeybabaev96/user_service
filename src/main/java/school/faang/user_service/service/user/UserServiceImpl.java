package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.pojo.Person;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.CountryService;
import school.faang.user_service.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final CountryService countryService;
    private final CsvMapper csvMapper;
    private final ExecutorService executorService;
    private final Random random = new Random();

    @Override
    public void processPersonsFromFile(MultipartFile file) {
        try (InputStream fileInputStream = file.getInputStream()) {
            log.debug("Begin parsing file");
            List<Person> persons = parsePersonsFromInputStream(fileInputStream);
            log.debug("End parsing file. Read {} records", persons.size());

            log.debug("Begin transforming records to entities");
            List<User> users = mapToUsers(persons);
            log.debug("End transforming records to entities");

            userRepository.saveAll(users);
            log.debug("Entities saved into DB");
            log.info("Persons from file {} are imported", file.getOriginalFilename());
        } catch (IOException e) {
            log.error("Error in file {}", file, e);
            throw new RuntimeException("Failed to reading file", e);
        }
    }

    private List<User> mapToUsers(List<Person> persons) {
            return persons.stream()
                    .map(person -> CompletableFuture.supplyAsync(() -> {
                        User user = userMapper.toUserEntity(person);
                        user.setPassword(generatePassword());
                        user.setCountry(countryService.getOrCreateCountry(person.getCountry()));
                        return user;
                    }, executorService).exceptionally(error -> {
                        log.error("Error processing file", error);
                        throw new RuntimeException("Failed to process users", error);
                    })).map(CompletableFuture::join).toList();
    }

    private List<Person> parsePersonsFromInputStream(InputStream fileInputStream) {
        List<Person> persons;
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        try {
            MappingIterator<Person> personIterator = csvMapper.readerFor(Person.class)
                    .with(schema)
                    .readValues(fileInputStream);
            persons = personIterator.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return persons;
    }

    private String generatePassword() {
        return random
                .ints(10, 33, 122)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
