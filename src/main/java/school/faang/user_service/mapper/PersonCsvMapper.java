package school.faang.user_service.mapper;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.Person;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class PersonCsvMapper {

    public List<Person> toPersons(MultipartFile file) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
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

}
