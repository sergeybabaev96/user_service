package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.MenteeDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MenteeMapper {
    List<MenteeDto> menteesToMenteesDtos(List<User> users);
}
