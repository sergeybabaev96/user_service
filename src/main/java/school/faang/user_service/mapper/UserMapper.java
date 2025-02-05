package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.pojo.Person;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = MentorshipRequestMapper.class)
public interface UserMapper {

    UserDto toUserDto(User user);

    User toUserEntity(UserDto userDto);

    List<User> toUserEntities(List<Person> persons);

    default User toUserEntity(Person person) {
        String aboutMe = String.format(
                        "State: " + person.getState() + ";" +
                        "Faculty: " + person.getFaculty() + ";" +
                        "Year of study: " + person.getYearOfStudy() + ";" +
                        "Major: " + person.getMajor() + ";" +
                        "Employer: " + person.getEmployer());
        return User.builder()
                .username(person.getFirstName() + person.getLastName())
                .email(person.getEmail())
                .phone(person.getPhone())
                .city(person.getCity())
                .country(Country.builder().title(person.getCountry()).build())
                .aboutMe(aboutMe)
                .build();
    }
}
