package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventResponse;
import school.faang.user_service.dto.promotion.EventPromotionRequest;
import school.faang.user_service.dto.promotion.UserPromotionRequest;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/promotion")
@RequiredArgsConstructor
public class PromotionController {

    private final UserService userService;
    private final EventService eventService;

    @PostMapping("/userprofile")
    public ResponseEntity<Void> buyUserPromotion(@Valid @RequestBody UserPromotionRequest request) {
        userService.userPromotion(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/event")
    public ResponseEntity<Void> buyEvent(@Valid @RequestBody EventPromotionRequest request) {
        eventService.eventPromotion(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public List<UserDto> getPromotionUsers() {
        return userService.getPromotionUsers();
    }

    @GetMapping("/events")
    public List<EventResponse> getPromotionEvents() {
        return eventService.getPromotionEvents();
    }
}

