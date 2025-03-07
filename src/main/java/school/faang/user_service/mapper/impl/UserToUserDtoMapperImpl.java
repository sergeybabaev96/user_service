package school.faang.user_service.mapper.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.FollowerResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserToUserDtoMapper;

@Component
public class UserToUserDtoMapperImpl implements UserToUserDtoMapper {
    @Override
    public FollowerResponse userToUserDto(User user) {
        return new FollowerResponse(user.getId(), user.getUsername(),user.getEmail());
    }
}
