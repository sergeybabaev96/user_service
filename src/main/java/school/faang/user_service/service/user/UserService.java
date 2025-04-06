package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.avatar.AvatarType;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final int GOALS_PER_USER = 3;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CountryRepository countryRepository;
    private final UserAvatarService userAvatarService;

    public boolean isWithinGoalLimit(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        return user.getGoals().size() < GOALS_PER_USER;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    public User registerUser(UserRegistrationDto dto) {
        Country country = countryRepository.findById(dto.getCountryId())
                .orElseThrow(() -> new EntityNotFoundException("Country not found with id: " + dto.getCountryId()));

        User user = userMapper.toEntity(dto);

        user.setCountry(country);
        user.setActive(true);
        user.setExperience(0);

        userAvatarService.generateAvatarForNewUser(user, AvatarType.PNG);

        return userRepository.save(user);
    }
}