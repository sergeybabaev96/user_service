package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.file.FileUploadResponseDto;
import school.faang.user_service.pojo.Person;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.s3.S3Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Override
    public FileUploadResponseDto parseCsv(InputStream fileInputStream) {

        List<Person> persons;
        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        try {
            MappingIterator<Person> personIterator = mapper.readerWithSchemaFor(Person.class).readValue(fileInputStream);
            persons = personIterator.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

/*
        try {
            MappingIterator<Object[]> it = mapper
                    .reader(Object[].class)
                    .readValues(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
*/      int size = persons.size();

        return new FileUploadResponseDto();
    }

}
