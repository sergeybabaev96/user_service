package school.faang.user_service.service.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.csv.CsvUserDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CsvParsingService {

    private final CsvMapper csvMapper;

    public List<CsvUserDto> parseUsers(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            MappingIterator<CsvUserDto> it = csvMapper
                    .readerFor(CsvUserDto.class)
                    .with(schema)
                    .readValues(inputStream);
            return it.readAll();

        }
    }
}