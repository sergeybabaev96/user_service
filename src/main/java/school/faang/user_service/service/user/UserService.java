package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.Person;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
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
    private final UserMapper userMapper;
    private final CountryRepository countryRepository;

    public List<UserDto> createUsers(List<Person> persons) {
        List<UserDto> users = new ArrayList<>(persons.size());
        for (Person person : persons) {
            UserDto userDto = createUser(person);
            users.add(userDto);
        }
        return users;
    }

    public UserDto createUser(Person person) {
        User user = personUserMapper.toUser(person);
        user.setCountry(
                getOrCreateCountry(
                        person.getContactInfo().getAddress().getCountry()));

        user = userRepository.save(user);
        return userMapper.toDto(user);
    }


    private Country getOrCreateCountry(String country) {
        Optional<Country> countryFromRepository = getCountry(country);

        return countryFromRepository.orElseGet(() -> createCountry(country));
    }

    private Optional<Country> getCountry(String country) {
        return ((Collection<Country>) countryRepository.findAll()).stream()
                .filter(c -> country.equals(c.getTitle()))
                .findFirst();
    }

    private Country createCountry(String country) {
        Country entity = new Country();
        entity.setTitle(country);
        return countryRepository.save(entity);
    }
}
