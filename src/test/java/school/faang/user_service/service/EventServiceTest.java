package school.faang.user_service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(eventService, "cleanupBatch", 2);
        ReflectionTestUtils.setField(eventService, "executor", Executors.newSingleThreadExecutor());
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

        ArgumentCaptor<List<Event>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventRepository).saveAll(captor.capture());
        List<Event> savedEvents = captor.getValue();
        assertEquals(1, savedEvents.get(0).getAttendees().size());
        assertEquals(2L, savedEvents.get(0).getAttendees().get(0).getId());
        assertTrue(savedEvents.get(1).getAttendees().isEmpty());
    }

    @Test
    public void testCleanPastEvents() {
        Event event1 = new Event();
        Event event2 = new Event();
        Event event3 = new Event();
        Event event4 = new Event();
        Event event5 = new Event();
        List<Event> batch1 = List.of(event1, event2);
        List<Event> batch2 = List.of(event3, event4);
        List<Event> batch3 = List.of(event5);

        Page<Event> page1 = new PageImpl<>(batch1);
        Page<Event> page2 = new PageImpl<>(batch2);
        Page<Event> page3 = new PageImpl<>(batch3);
        when(eventRepository.countByStatusIn(List.of(EventStatus.CANCELED, EventStatus.COMPLETED))).thenReturn(5L);
        when(eventRepository.findPastEventsWithPagination(any(Pageable.class), anyList()))
                .thenReturn(page1)
                .thenReturn(page2)
                .thenReturn(page3);
        List<CompletableFuture<Void>> futures = eventService.cleanPastEvents();
        verify(eventRepository, times(3)).findPastEventsWithPagination(any(Pageable.class), anyList());
        verify(eventRepository, times(1)).countByStatusIn(anyList());
        verify(eventRepository, times(3)).deleteAll(anyList());
        assertTrue(futures.stream().allMatch(CompletableFuture::isDone));
    }

    @Test
    public void testCleanPastEvents_WhenAnyException() {
        Event event1 = new Event();
        Event event2 = new Event();
        Event event5 = new Event();
        List<Event> batch1 = List.of(event1, event2);
        List<Event> batch3 = List.of(event5);

        Page<Event> page1 = new PageImpl<>(batch1);
        Page<Event> page3 = new PageImpl<>(batch3);

        when(eventRepository.countByStatusIn(List.of(EventStatus.CANCELED, EventStatus.COMPLETED))).thenReturn(5L);
        when(eventRepository.findPastEventsWithPagination(any(Pageable.class), anyList()))
                .thenReturn(page1)
                .thenThrow(new RuntimeException())
                .thenReturn(page3);
        List<CompletableFuture<Void>> futures = eventService.cleanPastEvents();
        verify(eventRepository, times(3)).findPastEventsWithPagination(any(Pageable.class), anyList());
        verify(eventRepository, times(1)).countByStatusIn(anyList());
        verify(eventRepository, times(2)).deleteAll(anyList());
        assertTrue(futures.stream().allMatch(CompletableFuture::isDone));
    }
}
