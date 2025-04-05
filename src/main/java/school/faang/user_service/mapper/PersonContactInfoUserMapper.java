package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.person.ContactInfo;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PersonContactInfoUserMapper {

    @Named("mapContactInfo")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "address.street", ignore = true)
    @Mapping(target = "address.city", source = "user.city")
    @Mapping(target = "address.state", ignore = true)
    @Mapping(target = "address.country", source = "user.country.title")
    @Mapping(target = "address.postalCode", ignore = true)
    ContactInfo toContactInfo(User user);
}
