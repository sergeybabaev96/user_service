package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final List<UserFilter> filters;

    public Stream<User> getPremiumUsers(UserFilterDto presetFilterDto) {
        Stream<User> users = userRepository.findPremiumUsers();
        if (presetFilterDto == null) {
            return users;
        } else {
            return filters.stream()
                    .filter(f -> f.isApplicable(presetFilterDto))
                    .flatMap(f -> f.apply(users, presetFilterDto))
                    .distinct();
        }
    }
}
