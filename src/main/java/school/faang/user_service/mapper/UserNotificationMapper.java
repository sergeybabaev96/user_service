package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserNotificationDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserNotificationMapper {

    @Mapping(target="id", source = "id")
    @Mapping(target="username", source = "username")
    @Mapping(target="email", source = "email")
    @Mapping(target="phone", source = "phone")
    @Mapping(target="preferredContact", source = "contactPreference.preference")
    UserNotificationDto toDto(User User);
}
