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
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final int MAX_THREAD_POOL_SIZE = 10;

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final CountryService countryService;
    private final CsvMapper csvMapper;
    private final Random random = new Random();

    @Override
    public void processPersonsFromFile(MultipartFile file) {
        try (InputStream fileInputStream = file.getInputStream()) {
            log.info("Begin parsing file");
            List<Person> persons = parsePersonsFromInputStream(fileInputStream);
            log.info("End parsing file. Read {} records", persons.size());

            log.info("Begin transforming records to entities");
            List<User> users = mapToUsers(persons);
            log.info("End transforming records to entities");

            userRepository.saveAll(users);
            log.info("Entities saved into DB");
        } catch (IOException e) {
            log.error("Error in file {}", file, e);
            throw new RuntimeException("Failed to reading file", e);
        }
    }

    private List<User> mapToUsers(List<Person> persons) {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_POOL_SIZE);
        try {
            List<CompletableFuture<User>> futureUsers = persons.stream()
                    .map(person -> CompletableFuture.supplyAsync(() -> {
                        User user = userMapper.toUserEntity(person);
                        user.setPassword(generatePassword());
                        user.setCountry(countryService.getOrCreateCountry(person.getCountry()));
                        return user;
                    }, executor)).toList();

            CompletableFuture<Void> allFuturesResult =
                    CompletableFuture.allOf(futureUsers.toArray(new CompletableFuture[0]));

            CompletableFuture<List<User>> completableFutureUsers = allFuturesResult.thenApply(v ->
                    futureUsers.stream()
                            .map(CompletableFuture::join)
                            .toList()
            ).exceptionally(error -> {
                log.error("Error processing file", error);
                throw new RuntimeException("Failed to process users", error);
            });

            return completableFutureUsers.join();
        } finally {
            executor.shutdown();
        }
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
