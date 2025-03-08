package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.Person;
import school.faang.user_service.entity.User;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PersonUserMapper {

    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "contactInfo", ignore = true)
    @Mapping(target = "education", ignore = true)
    @Mapping(target = "employer", ignore = true)
    @Mapping(target = "contactInfo.email", ignore = true)
    @Mapping(target = "contactInfo.phone", ignore = true)
    @Mapping(target = "contactInfo.address.street", ignore = true)
    @Mapping(target = "contactInfo.address.city", ignore = true)
    @Mapping(target = "contactInfo.address.state", ignore = true)
    @Mapping(target = "contactInfo.address.country", ignore = true)
    @Mapping(target = "contactInfo.address.postalCode", ignore = true)
    @Mapping(target = "education.faculty", ignore = true)
    @Mapping(target = "education.yearsOfStudy", ignore = true)
    @Mapping(target = "education.major", ignore = true)
    @Mapping(target = "education.GPA", ignore = true)
    Person toPerson(User user);


    @Mapping(target = "username", source = "person", qualifiedByName = "getUsername")
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "phone", source = "contactInfo.phone")
    @Mapping(target = "city", source = "contactInfo.address.city")
    @Mapping(target = "country", ignore = true) //, source = "contactInfo.address.country")
    @Mapping(target = "aboutMe", source = "person", qualifiedByName = "getAboutMe")
    @Mapping(target = "password", expression = "java(\"password123\")")

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "experience", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "followees", ignore = true)
    @Mapping(target = "ownedEvents", ignore = true)
    @Mapping(target = "mentees", ignore = true)
    @Mapping(target = "mentors", ignore = true)
    @Mapping(target = "receivedMentorshipRequests", ignore = true)
    @Mapping(target = "sentMentorshipRequests", ignore = true)
    @Mapping(target = "sentGoalInvitations", ignore = true)
    @Mapping(target = "receivedGoalInvitations", ignore = true)
    //@Mapping(target = "setGoals", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "participatedEvents", ignore = true)
    @Mapping(target = "recommendationsGiven", ignore = true)
    @Mapping(target = "recommendationsReceived", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "userProfilePic", ignore = true)
    @Mapping(target = "contactPreference", ignore = true)
    @Mapping(target = "premium", ignore = true)
    User toUser(Person person);


    @Named("getUsername")
    default String getUsername(Person person) {
        return String.format("%s %s", person.getFirstName(), person.getLastName());
    }

    @Named("getAboutMe")
    default String getAboutMe(Person person) {
        Map<String, Optional<String>> infos = new LinkedHashMap<>();
        infos.put("state", Optional.ofNullable(person.getContactInfo().getAddress().getState()));
        infos.put("faculty", Optional.ofNullable(person.getEducation().getFaculty()));
        infos.put("years of study", Optional.ofNullable(person.getEducation().getYearsOfStudy()));
        infos.put("major", Optional.ofNullable(person.getEducation().getMajor()));
        infos.put("employer", Optional.ofNullable(person.getEmployer()));

        return infos.entrySet().stream()
                .filter(entry -> entry.getValue().isPresent())
                .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue().get()))
                .collect(Collectors.joining(", "));
    }

}
