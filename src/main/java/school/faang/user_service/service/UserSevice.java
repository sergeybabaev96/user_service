package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserSevice {
    private final UserRepository repository;

    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }
}
