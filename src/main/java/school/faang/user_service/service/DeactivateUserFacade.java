package school.faang.user_service.service;

import org.springframework.web.bind.annotation.RequestParam;
import school.faang.user_service.dto.UserDto;

public interface DeactivateUserFacade {

    UserDto deactivateUser(long userId);
}
