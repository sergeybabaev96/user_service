package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;


public interface UserService {
    boolean doesUserExist(long userId);

    User getUserById(long userId);

    User findById(long userId);

    void checkUserExists(Long userId);

    boolean existsById(long userId);

    UserDto getUser(long userId);

    List<UserDto> getUsersByIds(List<Long> ids);

    public User getReferenceById(long userId);

}
