package school.faang.user_service.controller.user;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.FollowerService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/followers")
public class FollowerController {

    private final FollowerService followerService;

    @GetMapping("/{userId}")
    public List<UserDto> getFollowersByUserId(@PathVariable @NotNull @Min(0) long userId) {
        return followerService.getFollowersByUserId(userId);
    }
}
