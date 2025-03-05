package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "mentors", target = "mentorIds", expression ="java(toMentorsId(mentors))")
    UserDto toDto(User user);

//    @Mapping(source = "mentorIds", target = "mentors", expression = )
//    User toEntity(UserDto userDto);

    default List<Long> toMentorsId(List<User> mentors){
        return mentors.stream().map(User::getId).toList();
    }
}