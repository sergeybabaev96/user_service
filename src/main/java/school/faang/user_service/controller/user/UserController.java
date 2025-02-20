package school.faang.user_service.controller.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;

@Controller
@AllArgsConstructor
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    private static final String NEGATIVE_ID = "userId is negative";

    @PostMapping("/deactivate")
    @ResponseBody
    public void deactivateUser(@RequestParam Long userId) {
        if (idIsValid(userId)) {
            userService.deactivateUser(userId);
        } else {
            log.error(NEGATIVE_ID);
            throw new DataValidationException(NEGATIVE_ID);
        }
    }

    private boolean idIsValid(Long id) {
        return id >= 0;
    }
}
