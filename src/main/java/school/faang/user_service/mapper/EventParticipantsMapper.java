package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface  EventParticipantsMapper {
    @Mapping(source = "country.title", target = "country")
    UserDto toDto(User entity);
}
