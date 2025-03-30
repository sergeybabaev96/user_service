package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserNotificationDto;
import school.faang.user_service.dto.UserRegisterRequest;
import school.faang.user_service.dto.UserRegisterResponse;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.model.Person;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {
    User toEntity(UserDto dto);

    @Mapping(source = "countryId", target = "country.id")
    @Mapping(target = "ratingPoints", constant = "0")
    User toEntity(UserRegisterRequest request);

    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "aboutMe", source = "aboutMe")
    @Mapping(target = "ratingPoints", constant = "0")
    User toEntity(Person person, String username, String password, Country country, String aboutMe);

    UserDto toDto(User entity);

    UserRegisterResponse toUserRegisterResponse(User user);

    @Mapping(source = "contactPreference.preference", target = "preference")
    UserNotificationDto toUserNotificationDto(User user);
}