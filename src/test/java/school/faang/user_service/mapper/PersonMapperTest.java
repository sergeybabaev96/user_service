package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.csv.model.person.Address;
import school.faang.user_service.csv.model.person.ContactInfo;
import school.faang.user_service.csv.model.person.Education;
import school.faang.user_service.csv.model.person.Person;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.*;

class PersonMapperTest {

    private final PersonMapper mapper = Mappers.getMapper(PersonMapper.class);

    @Test
    void toUser() {
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setEmployer("XYZ Corp");

        Address address = new Address();
        address.setCity("New York");
        address.setState("NY");

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail("john.doe@example.com");
        contactInfo.setPhone("1234567890");
        contactInfo.setAddress(address);
        person.setContactInfo(contactInfo);

        Education education = new Education();
        education.setFaculty("Computer Science");
        education.setMajor("Software Engineering");
        education.setYearOfStudy(3);
        person.setEducation(education);

        Country country = new Country();
        country.setTitle("USA");

        User user = mapper.toUser(person, country);

        assertEquals("JohnDoe", user.getUsername());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("1234567890", user.getPhone());
        assertEquals("New York", user.getCity());
        assertEquals("USA", user.getCountry().getTitle());

        assertTrue(user.isActive());
        assertNotNull(user.getPassword());
        assertFalse(user.getPassword().isEmpty());

        String expectedAboutMe = "NY, Computer Science, 3 курс, Software Engineering, XYZ Corp";
        assertEquals(expectedAboutMe, user.getAboutMe());

    }
}