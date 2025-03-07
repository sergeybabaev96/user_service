package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MentorshipMapper {

    @Mapping(target = "userId", source = "entity.id")
    @Mapping(target = "userName", source = "entity.username")
    MentorshipDto toDto(User entity);

    @Mapping(target = "id", source = "dto.userId")
    @Mapping(target = "username", source = "dto.userName")
    User toEntity(MentorshipDto dto);

    List<MentorshipDto> toDtos(List<User> entities);
}
