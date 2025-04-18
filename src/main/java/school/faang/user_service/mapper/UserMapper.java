package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.entity.User;

import java.util.Locale;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "menteesIds", ignore = true)
    @Mapping(target = "mentorsIds", ignore = true)
    UserViewDto toViewDto(User user);

    default UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .preference(user.getPreference())
                .locale(Locale.forLanguageTag(String.valueOf(user.getLocale())))
                .build();
    }
}