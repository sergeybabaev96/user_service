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
import school.faang.user_service.service.country.CountryService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PersonUserMapper personUserMapper;
    private final CountryService countryService;

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
                countryService.getOrCreateCountry(
                        person.getContactInfo().getAddress().getCountry()));

        return userRepository.save(user);
    }
}
