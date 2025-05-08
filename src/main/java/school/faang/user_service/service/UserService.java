package school.faang.user_service.service;

import school.faang.user_service.entity.User;

import java.util.List;

public interface UserService {
    User findById(long id);

    void updateAll(List<User> users);

    User updateUser(User userToSave);
}
