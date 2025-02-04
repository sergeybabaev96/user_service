package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    public Stream<User> getPremiumUsers(UserFilterDto filter) {
        List<User> users =  userRepository.findPremiumUsers().toList();
    }
}
