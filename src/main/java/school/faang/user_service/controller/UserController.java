package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    public List<UserDto> getPremiumUsersByFilters(UserFilterDto userFilterDto) {

        return userService.getPremiumUsersByFilters(userFilterDto);


    }

    public List<UserDto> getAllUsersByFilters(UserFilterDto userFilterDto) {

        return userService.getAllUsersByFilters(userFilterDto);
    }
}
