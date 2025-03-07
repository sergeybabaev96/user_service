package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.MentorshipResponseDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.notification.UserChatIdUpdateDto;
import school.faang.user_service.dto.notification.UserNotificationDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    List<UserDto> usersToUserDtos(List<User> users);

    void updateUserChatId(@MappingTarget User user, UserChatIdUpdateDto userChatIdUpdateDto);

    UserNotificationDto toNotificationDto(User userById);

    List<MentorshipResponseDto> toMentorshipDtos(List<User> users);
}
