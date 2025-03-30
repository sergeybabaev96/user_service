package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.preson.PersonAboutDto;
import school.faang.user_service.dto.preson.PersonContactDto;
import school.faang.user_service.dto.preson.PersonDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public class CsvMapper {

    public User toUser(PersonDto personDto, PersonContactDto personContactDto, PersonAboutDto personAboutDto) {
        User user = new User();

        personDtoToUser(personDto, user);
        personContactDtoToUser(personContactDto, user);
        personAboutDtoToUser(personAboutDto, user);

        return user;
    }

    private void personDtoToUser(PersonDto personDto, User user) {
        user.setUsername(String.join(" ", (personDto.getFirstName() + personDto.getLastName())));
    }

    private void personContactDtoToUser(PersonContactDto personContactDto, User user) {
        user.setEmail(personContactDto.getEmail());
        user.setPhone(personContactDto.getPhone());
        user.setCity(personContactDto.getCity());
        user.setCountry(personContactDto.getCountry());
    }

    private void personAboutDtoToUser(PersonAboutDto personAboutDto, User user) {
        user.setAboutMe(String.join(". ",
                personAboutDto.getEmployer() +
                personAboutDto.getFaculty() +
                personAboutDto.getMajor() +
                personAboutDto.getYearOfStudy() +
                personAboutDto.getState()));
    }

}
