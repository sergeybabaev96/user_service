package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeleteEventByUserId() {

        Long userId = 1L;
        List<Event> events = List.of(new Event(), new Event());

        when(eventRepository.findAllByUserId(userId)).thenReturn(events);

        eventService.deleteEventByUserId(userId);

        verify(eventRepository).deleteAll(events);
    }

    @Test
    void testDeleteParticipationFromEvent() {

        Long userId = 1L;
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        Event event1 = new Event();
        event1.setAttendees(List.of(user1, user2));
        Event event2 = new Event();
        event2.setAttendees(List.of(user1));

        List<Event> events = List.of(event1, event2);

        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(events);

        eventService.deleteParticipationFromEvent(userId);

        ArgumentCaptor<List<Event>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventRepository).saveAll(captor.capture());

        List<Event> savedEvents = captor.getValue();

        assertEquals(1, savedEvents.get(0).getAttendees().size());
        assertEquals(2L, savedEvents.get(0).getAttendees().get(0).getId());

        assertTrue(savedEvents.get(1).getAttendees().isEmpty());
    }
}
