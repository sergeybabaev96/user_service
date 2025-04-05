package school.faang.user_service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.PremiumUserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PremiumUserMapper {
    @Mapping(source = "country", target = "country", qualifiedByName = "makeCountry")
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
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
    User toEntity(PremiumUserDto premiumUserDto);

    @InheritInverseConfiguration
    @Mapping(target = "country", expression = "java(user.getCountry().getTitle())")
    PremiumUserDto toDto(User user);

    @Named("makeCountry")
    default Country makeCountry(String country) {
        Country countryObj = new Country();
        countryObj.setTitle(country);
        return countryObj;
    }
}
