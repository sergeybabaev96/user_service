package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.event.EventPrepareData.getEventDto;
import static school.faang.user_service.utils.event.EventPrepareData.getFilterLocationDto;


@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @Test
    public void testCreateEvent() {
        EventDto eventDto = getEventDto();
        when(eventService.create(eq(eventDto))).thenReturn(eventDto);

        EventDto resultEventDto = eventController.create(eventDto);

        verify(eventService).create(eq(eventDto));
        assertEquals(eventDto, resultEventDto);
    }

    @Test
    public void testGetEvent() {
        EventDto eventDto = getEventDto();
        when(eventService.getEvent(eq(1L))).thenReturn(eventDto);

        EventDto resultEventDto = eventController.getEvent(1L);

        verify(eventService).getEvent(eq(1L));
        assertEquals(eventDto, resultEventDto);
    }

    @Test
    public void testGetEventsByFilter() {
        EventFilterDto filterDto = getFilterLocationDto();
        EventDto eventDto = getEventDto();
        when(eventService.getEventByFilters(eq(filterDto))).thenReturn(List.of(eventDto));

        List<EventDto> resultEventsDto = eventController.getEventsByFilter(filterDto);

        verify(eventService).getEventByFilters(eq(filterDto));
        assertEquals(List.of(eventDto), resultEventsDto);
    }

    @Test
    public void testDeleteEvent() {
        doNothing().when(eventService).deleteEvent(anyLong());

        eventController.deleteEvent(1L);

        verify(eventService).deleteEvent(anyLong());
    }

    @Test
    public void testUpdateEvent() {
        EventDto expectedEventDto = getEventDto();
        when(eventService.updateEvent(eq(expectedEventDto))).thenReturn(expectedEventDto);

        EventDto actualEventDto = eventController.updateEvent(expectedEventDto);

        verify(eventService).updateEvent(eq(expectedEventDto));
        assertEquals(expectedEventDto, actualEventDto);
    }

    @Test
    public void testGetOwnedEvent() {
        when(eventService.getOwnedEvents(anyLong())).thenReturn(List.of(getEventDto()));

        List<EventDto> ownedEvents = eventController.getOwnedEvents(1L);

        verify(eventService).getOwnedEvents(anyLong());
        assertEquals(1, ownedEvents.size());
    }

    @Test
    public void testGetParticipatedEvents() {
        when(eventService.getParticipatedEvents(anyLong())).thenReturn(List.of(getEventDto()));

        List<EventDto> ownedEvents = eventController.getParticipatedEvents(1L);

        verify(eventService).getParticipatedEvents(anyLong());
        assertEquals(1, ownedEvents.size());
    }
}