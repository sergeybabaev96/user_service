package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;

    public List<UserDto> getAllUsersByFilters(UserFilterDto userFilterDto) {
        List<User> users =  userRepository.findAll();
        for(UserFilter filter : userFilters){
            if(filter.isApplicable(userFilterDto)){
                users = filter.apply(users, userFilterDto);
            }
        }
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserDto> getPremiumUsersByFilters(UserFilterDto userFilterDto) {
        List<User> users =  userRepository.findPremiumUsers().toList();
        for(UserFilter filter : userFilters){
            if(filter.isApplicable(userFilterDto)){
                users = filter.apply(users, userFilterDto);
            }
        }
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }
}
