package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.impl.EventServiceImpl;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    public void testDeletingEventWithPlannedStatus() {
        long userId = 1;
        long eventId = 1;
        when(eventRepository.findAllByUserId(userId))
                .thenReturn(List.of(Event.builder().id(eventId).status(EventStatus.PLANNED).build()));

        eventService.deactivateEventsByUserId(userId);

        verify(eventRepository).deleteAllById(List.of(eventId));
    }

    @Test
    public void testDeletingEventWithNotPlannedStatus() {
        long userId = 1;
        long eventId = 1;
        when(eventRepository.findAllByUserId(userId))
                .thenReturn(List.of(Event.builder().id(eventId).status(EventStatus.IN_PROGRESS).build()));

        eventService.deactivateEventsByUserId(userId);

        verify(eventRepository, never()).deleteAllById(List.of());
    }
}
