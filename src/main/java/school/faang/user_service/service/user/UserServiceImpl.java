package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.file.FileUploadResponseDto;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final CountryService countryService;

    @Override
    public FileUploadResponseDto parseCsv(InputStream fileInputStream) {
        try {
            log.info("Begin parsing file");
            List<Person> persons = parsePersonsFromInputStream(fileInputStream);
            log.info("End parsing file. Read {} records", persons.size());

            log.info("Begin convertation records to entities");
            List<CompletableFuture<User>> futureUsers = persons.stream()
                    .map(person -> CompletableFuture.supplyAsync(() -> {
                        User user = userMapper.toUserEntity(person);
                        user.setPassword(generatePassword());
                        user.setCountry(countryService.getOrCreateCountry(person.getCountry()));
                        return user;
                    })).toList();

            List<User> users = futureUsers.stream()
                    .map(CompletableFuture::join)
                    .toList();
            log.info("End convertation records to entities");

            userRepository.saveAll(users);
            log.info("Entities saved into DB");

        }catch(Exception e)
        {
            return FileUploadResponseDto.builder()
                    .status("Error")
                    .error(e.toString())
                    .build();
        }
        return FileUploadResponseDto.builder()
                .status("OK")
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
