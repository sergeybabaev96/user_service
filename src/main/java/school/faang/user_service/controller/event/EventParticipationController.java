package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

@Slf4j
@Component
@RequiredArgsConstructor
@Validated

public class EventParticipationController {
    private final EventParticipationService service;

    @PostMapping("register")
    public void registerParticipant(@RequestBody EventDto eventDto, @RequestBody UserDto userDto) {
        service.registerParticipant(eventDto.getId(), userDto.getId());

    }

    @PostMapping("unregister")
    public void unregisterParticipant(@RequestBody EventDto eventDto, @RequestBody UserDto userDto) {
        service.unregisterParticipant(eventDto.getId(), userDto.getId());
    }

    @PostMapping("participants")
    public void getParticipant(@RequestBody EventDto eventDto) {
        service.getParticipant(eventDto.getId());
    }

    @PostMapping("participants/count")
    public void getParticipantsCount(@RequestBody EventDto eventDto) {

        service.getParticipantsCount(eventDto.getId());
    }

}
