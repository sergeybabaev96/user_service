package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.ProfileViewEventService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class ProfileViewEventController {
    private final ProfileViewEventService profileViewEventService;
    @PostMapping("/{viewerId}/{profileOwnerId}")
    public void viewProfile(@PathVariable("viewerId") long viewerId, @RequestParam("profileOwnerId") long profileOwnerId) {

        profileViewEventService.publishProfileViewEvent(viewerId, profileOwnerId);
    }
}
