package school.faang.user_service.databuilder.event;

import lombok.experimental.UtilityClass;
import school.faang.user_service.entity.User;

@UtilityClass
public class UserBuilder {
    public static User createValidUser(Long id) {
        return User.builder()
                .id(id)
                .build();
    }
}