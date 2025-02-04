package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegisterDto;
import school.faang.user_service.dto.user.UserResponseRegisterDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserRegisterDto userRegisterDto);

    @Mapping(source = "country.id", target = "countryId")
    @Mapping(source = "userProfilePic.fileId", target = "avatarId")
    @Mapping(source = "userProfilePic.smallFileId", target = "avatarSmallId")
    UserResponseRegisterDto toResponseRegisterDto(User user);

}
