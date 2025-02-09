package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.properties.UserServiceProperties;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${user-service.api-version}/tariff")
public class TariffController {

    private final UserServiceProperties userServiceProperties;

    @GetMapping("/available-tariffs")
    public List<TariffDto> getAvailableTariffs() {
        return userServiceProperties.getListAvailableTariffDtos();
    }
}
