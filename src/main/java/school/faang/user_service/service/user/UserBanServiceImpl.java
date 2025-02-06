package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBanServiceImpl implements UserBanService {
    private final UserRepository userRepository;

    @Override
    public void banUsers(String authorId) {
        User user = userRepository.findById(Long.valueOf(authorId)).orElseThrow(() ->
                new EntityNotFoundException(String.format("User with id = %s not found", authorId)));
        log.info("User with id = {} found", authorId);
        user.setBanned(true);
        userRepository.save(user);
        log.info("User with id = {} was banned", authorId);
    }
}
