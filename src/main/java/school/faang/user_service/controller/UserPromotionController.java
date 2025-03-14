package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.promotion.UserDto;
import school.faang.user_service.dto.promotion.UserPromotionRequestDto;
import school.faang.user_service.service.UserPromotionService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/promotion/user")
public class UserPromotionController {
    private final UserPromotionService userPromotionService;

    @PostMapping("/start")
    public ResponseEntity<String> startUserPromotion(
            @RequestBody UserDto userDto,
            @RequestBody UserPromotionRequestDto userPromotionRequestDto,
            @RequestParam("CurrencyDto") CurrencyDto currencyDto) {

        log.info("Received request to start user promotion. userId: {}, userPromotionRequestDto: {}",
                userDto.userId(), userPromotionRequestDto);

        ResponseEntity<String> response = userPromotionService.processStartUserPromotion(userDto,
                userPromotionRequestDto, currencyDto);

        log.info("User promotion started successfully. userId: {}", userDto.userId());
        return response;
    }

    @PostMapping("/end")
    public ResponseEntity<String> endUserPromotion(
            @RequestBody UserDto userDto,
            @RequestBody UserPromotionRequestDto userPromotionRequestDto) {

        log.info("Received request to end user promotion. userId: {}, userPromotionRequestDto: {}",
                userDto.userId(), userPromotionRequestDto);

        ResponseEntity<String> response = userPromotionService.processEndUserPromotion(userDto,
                userPromotionRequestDto);

        log.info("User promotion ended successfully. userId: {}", userDto.userId());
        return response;
    }

    @PostMapping("/update/priority")
    public ResponseEntity<String> updateUserPromotionPriority(
            @RequestBody UserDto userDto,
            @RequestBody UserPromotionRequestDto userPromotionRequestDto,
            @RequestParam("CurrencyDto") CurrencyDto currencyDto) {

        log.info("Received request to update user promotion priority. userId: {}, userPromotionRequestDto: {}",
                userDto.userId(), userPromotionRequestDto);

        ResponseEntity<String> response = userPromotionService.processUpdateUserPromotionPriority(userDto,
                userPromotionRequestDto, currencyDto);

        log.info("Successfully updated user promotion priority for userId: {}. New promotion priority: {}",
                userDto.userId(), userPromotionRequestDto.promotionPriority());
        return response;
    }

    @PostMapping("/update/type")
    public ResponseEntity<String> updateUserPromotionType(
            @RequestBody UserDto userDto,
            @RequestBody UserPromotionRequestDto userPromotionRequestDto,
            @RequestParam("CurrencyDto") CurrencyDto currencyDto) {

        log.info("Received request to update user promotion type. userId: {}, userPromotionRequestDto: {}",
                userDto.userId(), userPromotionRequestDto);

        ResponseEntity<String> response = userPromotionService.processUpdateUserPromotionType(userDto,
                userPromotionRequestDto, currencyDto);

        log.info("Successfully updated user promotion type for userId: {}. New promotion type: {}",
                userDto.userId(), userPromotionRequestDto.userPromotionType());
        return response;
    }
}