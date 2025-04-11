package school.faang.user_service.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private AutoCloseable closeable;

    @Captor
    private ArgumentCaptor<List<Event>> eventsCaptor;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testDeleteEventByUserId() {
        long userId = 1L;
        List<Event> events = List.of(new Event(), new Event());

        when(eventRepository.findAllByUserId(userId)).thenReturn(events);

        eventService.deleteEventByUserId(userId);

        verify(eventRepository).deleteAll(events);
    }

    @Test
    void testDeleteParticipationFromEvent() {
        long userId = 1L;
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();

        Event event1 = Event.builder().attendees(List.of(user1, user2)).build();
        Event event2 = Event.builder().attendees(List.of(user1)).build();
        List<Event> events = List.of(event1, event2);

        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(events);

        eventService.deleteParticipationFromEvent(userId);

        verify(eventRepository).saveAll(eventsCaptor.capture());
        List<Event> savedEvents = eventsCaptor.getValue();
        assertEquals(1, savedEvents.get(0).getAttendees().size());
        assertEquals(2L, savedEvents.get(0).getAttendees().get(0).getId());
        assertTrue(savedEvents.get(1).getAttendees().isEmpty());
    }
}
