package school.faang.user_service.service.parser;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.csv.PersonCsvDto;
import school.faang.user_service.exception.CsvParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CsvParserService {

    private final CsvMapper csvMapper;

    public List<PersonCsvDto> parseCsv(MultipartFile file) {
        CsvSchema schema = CsvSchema.emptySchema()
                .withHeader()
                .withColumnSeparator(',');

        try (InputStream is = file.getInputStream()) {
            MappingIterator<PersonCsvDto> it = csvMapper
                    .readerFor(PersonCsvDto.class)
                    .with(schema)
                    .readValues(is);
            return it.readAll();
        } catch (IOException e) {
            log.error(ParserErrorMessages.FAILED_CSV.getMessage(), e);
            throw new CsvParseException(ParserErrorMessages.FAILED_CSV.getMessage(), e);
        }
    }
}
