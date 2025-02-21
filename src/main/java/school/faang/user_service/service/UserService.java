package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.utility.aspect_annotations.UserProfileViewed;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @UserProfileViewed
    @Transactional
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("User with id #%d not found", id)));
    }

    @Transactional
    public List<User> getUsers(List<Long> ids) {
        log.info("Getting Users with ids {}", ids);
        return userRepository.findAllById(ids);
    }

    @Transactional
    public List<User> getUsersByIdsOrdered(List<Long> ids) {
        log.info("Getting Ordered Users with ids {}", ids);
        List<User> users = userRepository.findAllById(ids);

        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return ids.stream()
                .map(userMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Transactional
    public boolean isUserExistById(Long id) {
        return userRepository.existsById(id);
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
