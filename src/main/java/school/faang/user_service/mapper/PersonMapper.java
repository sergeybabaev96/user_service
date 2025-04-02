package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.csv.model.person.Person;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {

    @Mapping(target = "username", expression = "java(person.getFirstName() + person.getLastName())")
    @Mapping(target = "email", source = "person.contactInfo.email")
    @Mapping(target = "phone", source = "person.contactInfo.phone")
    @Mapping(target = "city", source = "person.contactInfo.address.city")
    @Mapping(target = "aboutMe", expression = "java(mapAboutMe(person))")
    @Mapping(target = "password", expression = "java(generatePassword())")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "education", ignore = true)
    User toUser(Person person, Country country);

    default String mapAboutMe(Person person) {
        StringBuilder sb = new StringBuilder();
        if (person.getContactInfo().getAddress().getState() != null && !person.getContactInfo().getAddress().getState().isEmpty()) {
            sb.append(person.getContactInfo().getAddress().getState()).append(", ");
        }
        sb.append(person.getEducation().getFaculty()).append(", ")
                .append(person.getEducation().getYearOfStudy()).append(" курс, ")
                .append(person.getEducation().getMajor());
        if (person.getEmployer() != null && !person.getEmployer().isEmpty()) {
            sb.append(", ").append(person.getEmployer());
        }
        return sb.toString();
    }

    default String generatePassword() {
        return UUID.randomUUID().toString();
    }
}
