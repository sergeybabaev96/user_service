package school.faang.user_service.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.UserViewDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "menteesIds", ignore = true)
    @Mapping(target = "mentorsIds", ignore = true)
    @Mapping(source = "contactPreference.preference", target = "preference")
    UserViewDto toViewDto(User user);
}