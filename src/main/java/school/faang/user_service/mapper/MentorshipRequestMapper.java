package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper(componentModel = "spring")
public interface MentorshipRequestMapper {

   MentorshipRequest dtoToMentorshipRequest(MentorshipRequestDto dto);
}
