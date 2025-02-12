package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.file.FileUploadResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.pojo.Person;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.CountryService;
import school.faang.user_service.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final CountryService countryService;

    @Override
    public FileUploadResponseDto processPersonsFromFile(MultipartFile file) {
        try (InputStream fileInputStream = file.getInputStream()) {
            log.info("Begin parsing file");
            List<Person> persons = parsePersonsFromInputStream(fileInputStream);
            log.info("End parsing file. Read {} records", persons.size());
            log.info("Begin transforming records to entities");

            List<CompletableFuture<User>> futureUsers = persons.stream()
                    .map(person -> CompletableFuture.supplyAsync(() -> {
                        User user = userMapper.toUserEntity(person);
                        user.setPassword(generatePassword());
                        user.setCountry(countryService.getOrCreateCountry(person.getCountry()));
                        return user;
                    })).toList();

            CompletableFuture<Void> allFuturesResult =
                    CompletableFuture.allOf(futureUsers.toArray(new CompletableFuture[futureUsers.size()]));

            CompletableFuture<List<User>> cf_users= allFuturesResult.thenApply(v ->
                    futureUsers.stream().
                            map(CompletableFuture::join)
                            .toList()
            ).exceptionally(err -> {
                log.error("Error processing file {}", file.getOriginalFilename());
                return new ArrayList<>();
            });

            List<User> users = cf_users.get();

            log.info("End transforming records to entities");

            userRepository.saveAll(users);
            log.info("Entities saved into DB");
        } catch (IOException | ExecutionException | InterruptedException e) {
            return FileUploadResponseDto.builder()
                    .status(String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY))
                    .error(e.toString())
                    .build();
        }
        return FileUploadResponseDto.builder()
                .status(String.valueOf(HttpStatus.OK))
                .build();
    }

    private List<Person> parsePersonsFromInputStream(InputStream fileInputStream) {
        List<Person> persons;
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        try {
            MappingIterator<Person> personIterator = mapper.readerFor(Person.class)
                    .with(schema)
                    .readValues(fileInputStream);
            persons = personIterator.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return persons;
    }

    private String generatePassword() {
        return new Random()
                .ints(10, 33, 122)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
