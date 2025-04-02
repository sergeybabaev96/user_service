package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.preson.PersonAboutDto;
import school.faang.user_service.dto.preson.PersonContactDto;
import school.faang.user_service.dto.preson.PersonDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class CsvMapperTest {
    private final Country country = Country.builder()
            .title("USA")
            .build();

    @InjectMocks
    CsvMapper csvMapper;

    @Test
    public void positiveMappingAllFields() {
        PersonDto personDto = PersonDto.builder()
                .lastName("Doe")
                .firstName("John")
                .build();

        PersonContactDto contactDto = PersonContactDto.builder()
                .email("john.doe@example.com")
                .phone("+1234567890")
                .city("New York")
                .country(country)
                .build();

        PersonAboutDto aboutDto = PersonAboutDto.builder()
                .employer("F")
                .faculty("A")
                .major("A")
                .yearOfStudy("N")
                .state("G")
                .build();

        User result = csvMapper.toUser(personDto, contactDto, aboutDto);

        assertNotNull(result);
        assertEquals("John Doe", result.getUsername());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("+1234567890", result.getPhone());
        assertEquals("New York", result.getCity());
        assertEquals(country, result.getCountry());
        assertEquals("F. A. A. N. G", result.getAboutMe());
    }
}
