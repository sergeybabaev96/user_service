package school.faang.user_service.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import school.faang.user_service.entity.User;

@Getter
@Setter
public class ProfilePicEvent extends ApplicationEvent {
    private User user;
    private String profilePicUrl;

    public ProfilePicEvent(Object source, String profilePicUrl, User user) {
        super(source);
        this.profilePicUrl = profilePicUrl;
        this.user = user;
    }
}
