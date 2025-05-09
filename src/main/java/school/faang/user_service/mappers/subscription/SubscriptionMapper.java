package school.faang.user_service.mappers.subscription;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.SubscriptionDto;
import school.faang.user_service.entity.User;

@Component
public class SubscriptionMapper {

    public SubscriptionDto userToSubscriptionDto(User user) {
        return new SubscriptionDto(
                user.getId(),
                user.getUsername(),
                user.getEmail());
    }

    public User SubscriptionDtoToUser(SubscriptionDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        return user;
    }
}
