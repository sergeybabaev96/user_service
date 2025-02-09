package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.GetEventRequest;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${user-service.api-version}/event")
public class EventController {
    private final EventService eventService;

    @PostMapping("/buy-tariff")
    public TariffDto buyTariff(@RequestBody BuyTariffRequest request) {
        return eventService.buyEventTariff(request.tariffDto(), request.id());
    }

    @GetMapping("/events")
    public List<EventDto> getEvents(@RequestBody GetEventRequest request) {
        return eventService.findEventByFilter(request);
    }
}
