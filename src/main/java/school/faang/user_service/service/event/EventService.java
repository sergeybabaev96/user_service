package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.GetEventRequest;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

public interface EventService {
    Event findEventById(Long id);

    TariffDto buyEventTariff(TariffDto tariffDto, Long eventId);

    List<EventDto> findEventByFilter(GetEventRequest request);
}
