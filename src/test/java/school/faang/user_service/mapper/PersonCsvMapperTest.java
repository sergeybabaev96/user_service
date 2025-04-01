package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.csv.model.person.*;
import school.faang.user_service.dto.csv.PersonCsvDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PersonCsvMapperTest {
    private final PersonCsvMapper mapper = Mappers.getMapper(PersonCsvMapper.class);

    @Test
    void toPerson() {
        PersonCsvDto dto = new PersonCsvDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john@example.com");
        dto.setPhone("1234567890");
        dto.setStreet("Main St");
        dto.setCity("New York");
        dto.setState("NY");
        dto.setCountry("USA");
        dto.setPostalCode("10001");

        dto.setFaculty("Computer Science");
        dto.setYearOfStudy(3);
        dto.setMajor("Software Engineering");
        dto.setGpa(3.75);

        dto.setDegree("Bachelor");
        dto.setInstitution("XYZ University");
        dto.setCompletionYear("2022");
        dto.setAdmissionDate("2018-09-01");
        dto.setGraduationDate("2022-06-01");

        Person person = mapper.toPerson(dto);

        assertEquals("John", person.getFirstName());
        assertEquals("Doe", person.getLastName());

        ContactInfo contactInfo = person.getContactInfo();
        assertNotNull(contactInfo);
        assertEquals("john@example.com", contactInfo.getEmail());
        assertEquals("1234567890", contactInfo.getPhone());

        Address address = contactInfo.getAddress();
        assertNotNull(address);
        assertEquals("Main St", address.getStreet());
        assertEquals("New York", address.getCity());
        assertEquals("NY", address.getState());
        assertEquals("USA", address.getCountry());
        assertEquals("10001", address.getPostalCode());

        Education education = person.getEducation();
        assertNotNull(education);
        assertEquals("Computer Science", education.getFaculty());
        assertEquals(3, education.getYearOfStudy());
        assertEquals("Software Engineering", education.getMajor());
        assertEquals(3.75, education.getGpa());

        Status status = person.getStatus();
        assertNotNull(status);
        assertEquals("2018-09-01", status.getAdmissionDate());
        assertEquals("2022-06-01", status.getGraduationDate());
        assertEquals(1, status.getPreviousEducation().size());

        PreviousEducation prev = status.getPreviousEducation().get(0);
        assertEquals("Bachelor", prev.getDegree());
        assertEquals("XYZ University", prev.getInstitution());
        assertEquals("2022", prev.getCompletionYear());
    }
}