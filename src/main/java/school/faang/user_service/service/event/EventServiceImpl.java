package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.GetEventRequest;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilter;
import school.faang.user_service.entity.Tariff;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.TariffMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.tariff.TariffService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final TariffService tariffService;
    private final EventRepository eventRepository;
    private final TariffMapper tariffMapper;
    private final List<EventFilter> eventFilters;
    private final EventMapper eventMapper;

    @Override
    public Event findEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));
    }

    @Override
    public TariffDto buyEventTariff(TariffDto tariffDto, Long eventId) {
        log.info("Start buy event tariff, eventId: {}", eventId);
        Event event = findEventById(eventId);

        tariffDto.setEventId(eventId);
        Tariff tariff = tariffService.buyTariff(tariffDto, event.getOwner().getId());
        event.setTariff(tariff);
        eventRepository.save(event);

        return tariffMapper.toDto(tariff);
    }

    @Override
    public List<EventDto> findEventByFilter(GetEventRequest request) {
        List<Event> events = eventRepository.findAllOrderByTariffAndLimit(request.getLimit(), request.getOffset());

        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(request.getFilter())) {
                events = eventFilter.apply(events, request.getFilter());
            }
        }

        return events.stream()
                .peek(event -> tariffService.decrementShows(event.getTariff()))
                .map(eventMapper::toDto)
                .toList();
    }
}
