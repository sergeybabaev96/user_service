package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.PremiumUserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.PremiumUserMapper;
import school.faang.user_service.service.UserService;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/premiumuser")
public class PremiumUserController {

    public final UserService userService;
    public final PremiumUserMapper premiumUserMapper;


    @GetMapping({"", "/"})
    public List<PremiumUserDto> getPremiumUsers() {
        return getPremiumUsers(null);
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.OK)
    public List<PremiumUserDto> getPremiumUsers(@RequestBody UserFilterDto userFilterDto) {
        return mapToPremiumUserDto(
                userService.getPremiumUsers(userFilterDto));
    }

    private List<PremiumUserDto> mapToPremiumUserDto(List<User> users) {
        return users.stream()
                .filter(Objects::nonNull)
                .map(premiumUserMapper::toDto)
                .toList();
    }
}