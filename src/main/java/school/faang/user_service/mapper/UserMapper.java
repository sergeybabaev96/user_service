package school.faang.user_service.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface UserMapper {

    @Mapping(target = "contactPreference.preference", source = "preference")
    User toEntity(UserDto dto);

    @Mapping(target = "preference", source = "contactPreference.preference")
    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);
}
