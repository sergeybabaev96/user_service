package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.Person;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    public List<UserDto> saveUsers(List<Person> persons) {
        return List.of();
    }
}
