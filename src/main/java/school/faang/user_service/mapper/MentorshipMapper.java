package school.faang.user_service.mapper;


import org.mapstruct.Mapper;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface MentorshipMapper {

    MentorshipDto toDto(User user);


}
