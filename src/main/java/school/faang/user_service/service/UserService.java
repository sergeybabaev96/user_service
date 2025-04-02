package school.faang.user_service.service;

import school.faang.user_service.entity.User;

public interface UserService {
    User getReferenceById(long userId);

    long findUniqueIdByUsername(String username);

    User findUserById(long userId);

    void checkUserExists(Long userId);

    boolean existsById(long userId);

}