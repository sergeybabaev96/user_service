package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.person.Person;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.PersonUserMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PersonUserMapper personUserMapper;
    private final CountryRepository countryRepository;

    public List<User> createUsers(List<Person> persons) {
        if (null == persons || persons.isEmpty()) {
            return List.of();
        }

        List<User> users = new ArrayList<>(persons.size());
        for (Person person : persons) {
            User user = createUser( person );
            users.add( user );
        }

        return users;
    }

    public User createUser(Person person) {
        if (null == person) {
            throw new DataValidationException("Person have to be not null");
        }

        User user = personUserMapper.toUser(person);
        user.setCountry(
                getOrCreateCountry(
                        person.getContactInfo().getAddress().getCountry()));

        return userRepository.save(user);
    }


    private Country getOrCreateCountry(String country) {
        Optional<Country> countryFromRepository = getCountry(country);

        return countryFromRepository.orElseGet(() -> createCountry(country));
    }

    private Optional<Country> getCountry(String country) {
        return countryRepository.findByTitle(country);
    }

    private Country createCountry(String country) {
        Country entity = new Country();
        entity.setTitle(country);
        return countryRepository.save(entity);
    }
}
