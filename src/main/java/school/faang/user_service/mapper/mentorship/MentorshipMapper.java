package school.faang.user_service.mapper.mentorship;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship.MentorshipDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipMapper {

    @Mapping(target = "menteeIds", expression = "java(mapIds(user.getMentees()))")
    @Mapping(target = "mentorIds", expression = "java(mapIds(user.getMentors()))")
    MentorshipDto toDto(User user);

    @InheritInverseConfiguration(name = "toDto")
    User toEntity(MentorshipDto mentorshipDto);

    default List<Long> mapIds(List<User> users) {
        return users == null ? null : users.stream()
                .map(User::getId)
                .toList();
    }
}
