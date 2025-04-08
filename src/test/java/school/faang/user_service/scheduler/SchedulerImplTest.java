package school.faang.user_service.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.EventServiceImpl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SchedulerImplTest {

    @Mock
    private EventServiceImpl eventService;

    @InjectMocks
    private SchedulerImpl scheduler;

    @Test
    void clearEvents_shouldCallEventService() {
        scheduler.clearEvents();

        verify(eventService,times(1)).cleanPastEvents();
    }
}
