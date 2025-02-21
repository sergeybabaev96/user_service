package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/events")
@RestController
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;
    private final UserMapper userMapper;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{eventId}/participants")
    public void registerParticipant(@PathVariable long eventId, @RequestParam long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{eventId}/participants")
    public void unregisterParticipant(@PathVariable long eventId, @RequestParam long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{eventId}/participants")
    public List<UserDto> getParticipants(@PathVariable long eventId) {
        List<User> users = eventParticipationService.getParticipants(eventId);
        return userMapper.toDtoList(users);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{eventId}/participants/count")
    public int getParticipantsCount(@PathVariable long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}