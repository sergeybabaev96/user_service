package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.user.UserRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBanServiceImpl implements UserBanService {
    private final UserRepository userRepository;

    @Override
    public void banUser(String authorId) {
        Optional<User> userOpt = userRepository.findById(Long.valueOf(authorId));
        if (userOpt.isPresent()) {
            log.info("User with id = {} found", authorId);
            userOpt.get().setBanned(true);
            userRepository.save(userOpt.get());
            log.info("User with id = {} was banned", authorId);
        } else {
            log.error("User with id = {} not found", authorId);
        }
    }
}
