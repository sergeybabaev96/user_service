package school.faang.user_service.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface MentorshipMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    MentorshipDto toDto(User user);


}
