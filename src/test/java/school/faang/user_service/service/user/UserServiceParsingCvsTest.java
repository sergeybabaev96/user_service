package school.faang.user_service.service.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.person.Address;
import school.faang.user_service.dto.person.ContactInfo;
import school.faang.user_service.dto.person.Person;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.PersonUserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.country.CountryService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceParsingCvsTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PersonUserMapper personUserMapper;
    @Mock
    private CountryService countryService;

    @InjectMocks
    private UserService userService;
    @Spy
    private UserService userServiceMock = new UserService(userRepository, personUserMapper, countryService);

    @ParameterizedTest
    @CsvSource({"1", "12", "121"})
    @DisplayName("createUsers(). Positive. Create Users from List<Person>")
    public void  createUsersFromListPersonPositive(int size) {
        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            persons.add(new Person());
        }

        Mockito.doReturn(new User()).when(userServiceMock).createUser(any(Person.class));
        List<User> users = userServiceMock.createUsers(persons);
        Mockito.verify(userServiceMock,Mockito.times(size)).createUser(any(Person.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("createUsers(). Negative. Create Users from List<Person>, when list is null and empty")
    public void  createUsersFromListPersonWhenListIsNullOrEmpty(List<Person> persons) {
        List<User> users = userServiceMock.createUsers(persons);
        Mockito.verify(userServiceMock,Mockito.never()).createUser(any(Person.class));
    }


    @Test
    @DisplayName("createUser(). Negative. Create User from Person, when Person is null")
    public void createUserFromPersonWhenPersonIsNull() {
        assertThrows(RuntimeException.class, () -> userService.createUser(null) );
    }

    @Test
    @DisplayName("createUser(). Positive. Create User from Person")
    public void createUserFromPerson() {
        String countryName = "Test Country";
        Person person = getPersonWithCountry(countryName);
        Country country = new Country(1, countryName, List.of());

        Mockito.when(personUserMapper.toUser(any(Person.class))).thenReturn(new User());
        Mockito.when(countryService.getOrCreateCountry(countryName)).thenReturn(country);

        User result = userService.createUser(person);

        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
        Mockito.verify(countryService, Mockito.times(1)).getOrCreateCountry(countryName);
    }


    private Person getPersonWithCountry(String country) {
        Address address = new Address();
        address.setCountry(country);

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setAddress(address);

        Person person = new Person();
        person.setContactInfo(contactInfo);

        return person;
    }
}