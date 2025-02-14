package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.pojo.Person;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = MentorshipRequestMapper.class)
public interface UserMapper {

    @Mapping(source = "user.id", target = "userId")
    UserDto toUserDto(User user);

    User toUserEntity(UserDto userDto);

    List<User> toUserEntities(List<Person> persons);

    @Mapping(target = "username", expression = "java(getUsername(person))")
    @Mapping(target = "country", expression = "java(getCountry(person))")
    @Mapping(target = "aboutMe", expression = "java(person.toString())")
    User toUserEntity(Person person);

    default String getUsername(Person person) {
        return person.getFirstName() + person.getLastName();
    }

    default Country getCountry(Person person) {
        return Country.builder().title(person.getCountry()).build();
    }
}
