package school.faang.user_service.controller.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.Person;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@Validated
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    @PostMapping
    public List<UserDto> parseCsvToUsers(
            @NotNull(message = "Request has to have a CSV file with Persons") @RequestBody MultipartFile file)
            throws IOException {

        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(Person.class).withHeader();

        List<Person> persons;

        try (InputStream inputStream = file.getInputStream()) {
            MappingIterator<Person> iterator;
            iterator = csvMapper
                    .readerFor(Person.class)
                    .with(schema)
                    .readValues(inputStream);
            persons = iterator.readAll();
        }

        return userService.saveUsers(persons);
    }
}
