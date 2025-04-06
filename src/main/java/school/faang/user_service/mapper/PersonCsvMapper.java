package school.faang.user_service.mapper;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.person.Person;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

@Component
public class PersonCsvMapper {

    private final CsvMapper csvMapper;
    private final Validator validator;

    public PersonCsvMapper() {
        csvMapper = new CsvMapper();
        ValidatorFactory factory = jakarta.validation.Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public List<Person> toPersons(MultipartFile file) throws IOException {
        if (null == file || file.isEmpty()) {
            throw new IllegalArgumentException();
        }

        CsvSchema schema = getCsvSchema();

        List<Person> persons;
        try (InputStream inputStream = file.getInputStream()) {
            MappingIterator<Person> iterator;
            iterator = csvMapper
                    .readerFor(Person.class)
                    .with(schema)
                    .readValues(inputStream);
            persons = iterator.readAll();
        }

        persons.forEach(this::validate);
        return persons;
    }

    private CsvSchema getCsvSchema() {
        return CsvSchema.builder()
                .addColumn("firstName")
                .addColumn("lastName")
                .addColumn("email")
                .addColumn("phone")
                .addColumn("street")
                .addColumn("city")
                .addColumn("state")
                .addColumn("country")
                .addColumn("postalCode")
                .addColumn("faculty")
                .addColumn("yearsOfStudy")
                .addColumn("major")
                .addColumn("GPA")
                .addColumn("employer")
                .setUseHeader(true)
                .build();
    }

    private void validate(Person person) {
        Set<ConstraintViolation<Person>> violations = validator.validate(person);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("CsvMapper validation error", violations);
        }
    }

}
