package school.faang.user_service.service.event.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilter;
import school.faang.user_service.dto.event.GetEventRequest;
import school.faang.user_service.entity.Tariff;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.TariffMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;
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
        return eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Event with id %s not found", id)));
    }

    @Override
    public TariffDto buyEventTariff(TariffDto tariffDto, Long eventId) {
        log.info("Start buy event tariff, eventId: {}", eventId);
        Event event = findEventById(eventId);

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

        events.stream()
                .filter(event -> event.getTariff() != null)
                .forEach(event -> tariffService.decrementShows(event.getTariff().getId()));

        return events.stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deactivateEventsByUserId(long userId) {
        List<Long> eventIds = eventRepository.findAllByUserId(userId)
                .stream()
                .filter(event -> event.getStatus() == EventStatus.PLANNED)
                .map(Event::getId)
                .toList();
        if (!eventIds.isEmpty()) {
            eventRepository.deleteUserParticipationByEventId(eventIds);
            eventRepository.deleteAllById(eventIds);
        }
    }
}
