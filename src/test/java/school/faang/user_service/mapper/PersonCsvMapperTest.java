package school.faang.user_service.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.person.Person;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonCsvMapperTest {

    PersonCsvMapper personCsvMapper = new PersonCsvMapper();

    @Test
    @DisplayName("Negative. Validation error")
    void toPersonsValidationError() throws IOException {
        assertThrows(jakarta.validation.ConstraintViolationException.class,
                () -> personCsvMapper.toPersons(getMultipartFile(CSV_INCORRECT_DATA)));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("getEmptyMultipartFiles")
    @DisplayName("Negative. File is empty or null")
    void toPersonsNullOrEmptyFile(MultipartFile file) throws IOException {
        assertThrows(IllegalArgumentException.class,
                () -> personCsvMapper.toPersons(file));
    }

    @Test
    @DisplayName("Positive. Two records")
    void toPersons() throws IOException {
        List<Person> persons = personCsvMapper.toPersons(getMultipartFile(CSV_CORRECT_DATA));

        assertEquals(2, persons.size());
    }

    @ParameterizedTest
    @MethodSource("getFastEmptyMultipartFiles")
    @DisplayName("Positive. Zero records")
    void toPersonZeroRecords(MultipartFile file) throws IOException {
        List<Person> persons = personCsvMapper.toPersons(file);

        assertEquals(0, persons.size());
    }


    private static final String CSV_CORRECT_DATA =
            "firstName,lastName,email,phone,street,city,state,country,postalCode,faculty,yearsOfStudy,major,GPA,employer\n" +
                    "John,Doe,john.doe@example.com,+123456789,123 Main St,New York,NY,USA,10001,Engineering,2010-2014,Computer Science,3.8,Google\n" +
                    "Alice,Smith,alice.smith@example.com,+987654321,456 Oak St,Los Angeles,CA,USA,90001,Business,2012-2016,Marketing,3.5,Amazon";
    private static final String CSV_INCORRECT_DATA =
            "firstName,lastName,email,phone,street,city,state,country,postalCode,faculty,yearsOfStudy,major,GPA,employer\n" +
                    ",,joh@n.do@e@e@xample.com,+123456789,123 Main St,New York,NY,USA,10001,Engineering,2010-2014,Computer Science,3.8,Google\n" +
                    "Alice,Smith,alice.smith@example.com,+987654321,456 Oak St,Los Angeles,CA,USA,90001,Business,2012-2016,Marketing,3.5,Amazon";
    private static final String CSV_FAST_EMPTY_1_DATA = "firstName,lastName,email,phone,street,city,state,country,postalCode,faculty,yearsOfStudy,major,GPA,employer";
    private static final String CSV_FAST_EMPTY_2_DATA = "firstName";
    private static final String CSV_FAST_EMPTY_3_DATA = "f";
    private static final String CSV_EMPTY_DATA = "";


    private static MultipartFile getMultipartFile(String text) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

        return new MockMultipartFile("file.txt", inputStream);
    }


    private static Iterable<MultipartFile> getEmptyMultipartFiles() throws IOException {
        List<MultipartFile> listOfFiles = new ArrayList<>();

        listOfFiles.add(getMultipartFile(CSV_EMPTY_DATA));

        return listOfFiles;
    }

    private static Iterable<MultipartFile> getFastEmptyMultipartFiles() throws IOException {
        List<MultipartFile> listOfFiles = new ArrayList<>();

        listOfFiles.add(getMultipartFile(CSV_FAST_EMPTY_1_DATA));
        listOfFiles.add(getMultipartFile(CSV_FAST_EMPTY_2_DATA));
        listOfFiles.add(getMultipartFile(CSV_FAST_EMPTY_3_DATA));


        return listOfFiles;
    }
}