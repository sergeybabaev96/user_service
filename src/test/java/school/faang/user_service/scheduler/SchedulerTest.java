package school.faang.user_service.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.service.event.EventService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SchedulerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private Scheduler scheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testClearEvents() {

        scheduler.clearEvents();

        verify(eventService, times(1)).clearEvents();
    }
}
