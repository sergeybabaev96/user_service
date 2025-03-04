package school.faang.user_service.service.user.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.ProfilePicEvent;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.user.GetUserRequest;
import school.faang.user_service.dto.user.UserFilter;
import school.faang.user_service.entity.Tariff;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.TariffMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.service.tariff.TariffService;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TariffMapper tariffMapper;
    private final List<UserFilter> userFilters;
    private final TariffService tariffService;
    private final S3Service s3Service;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ResponseEntity<UserDto> getUser(long userId) {
        log.info("Get user by id: {}", userId);
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public TariffDto buyUserTariff(TariffDto tariffDto, Long userId) {
        log.info("Start buy user tariff, userId: {}", userId);
        User user = findById(userId);
        if (user.getTariff() != null && user.getTariff().getIsActive()) {
            throw new BusinessException("User already has active tariff");
        }

        Tariff tariff = tariffService.buyTariff(tariffDto, userId);
        user.setTariff(tariff);
        userRepository.save(user);

        return tariffMapper.toDto(tariff);
    }

    @Override
    public List<UserDto> findUsersByFilter(GetUserRequest request) {
        List<User> users = userRepository.findAllOrderByTariffAndLimit(request.getLimit(), request.getOffset());

        for (UserFilter userFilter : userFilters) {
            if (userFilter.isApplicable(request.getFilter())) {
                users = userFilter.apply(users, request.getFilter());
            }
        }

        users.stream()
                .filter(user -> user.getTariff() != null)
                .forEach(user -> tariffService.decrementShows(user.getTariff().getId()));

        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    private User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %s not found", userId)));
    }

    @Transactional
    @Override
    public UserDto deactivateUser(long userId) {
        User user = userRepository
                .findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setActive(false);
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Override
    public void saveProfilePic(long userId, MultipartFile avatar) {
        log.info("Save profile pic for user with id: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        String key = s3Service.uploadFile(avatar);
        updateUserProfilePic(user, key);
        eventPublisher.publishEvent(new ProfilePicEvent(this, key, user));
    }

    private void updateUserProfilePic(User user, String key) {
        user.setUserProfilePic(new UserProfilePic(key, null));
        userRepository.save(user);
        log.info("Profile pic saved for user with id: {}", user.getId());
    }
}
