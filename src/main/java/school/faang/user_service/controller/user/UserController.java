package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.mapper.PersonCsvMapper;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@Validated
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;
    private final PersonCsvMapper personCsvMapper;

    @PostMapping("/csv")
    @Operation(
            summary = "Create Users from CSV file",
            description = "It parses CSV file. CSV file has to have column like Person.class/PersonCsvMapper.class")
    public List<UserDto> createUsersFromCsvFile(
            @NotNull(message = "Request has to have a CSV file with Persons") @RequestBody MultipartFile file)
            throws IOException {

        return userService.createUsers(
                personCsvMapper.toPersons(file));
    }
}
