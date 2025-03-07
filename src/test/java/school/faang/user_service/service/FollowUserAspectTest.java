package school.faang.user_service.service;

import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.kafka.FollowUserAspect;
import school.faang.user_service.service.kafka.KafkaProducer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FollowUserAspectTest {

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private JoinPoint joinPoint;

    @InjectMocks
    private FollowUserAspect followUserAspect;

    @Test
    public void testAfterFollowUserMethod_ValidArguments() {
        long followerId = 1L;
        long followeeId = 3L;
        Object[] args = new Object[]{followerId, followeeId};

        when(joinPoint.getArgs()).thenReturn(args);

        followUserAspect.afterFollowUserMethod(joinPoint, null);

        verify(kafkaProducer, times(1)).sendFollowUserEvent(followerId, followeeId);
    }

    @Test
    public void testAfterFollowUserMethod_WithExtraArguments() {
        long followerId = 11L;
        long followeeId = 22L;
        Object extra = "extra";
        Object[] args = new Object[]{followerId, followeeId, extra};

        when(joinPoint.getArgs()).thenReturn(args);

        followUserAspect.afterFollowUserMethod(joinPoint, null);

        verify(kafkaProducer, times(1)).sendFollowUserEvent(followerId, followeeId);
    }

    @Test
    public void testAfterFollowUserMethod_InvalidArgumentType() {
        Object[] args = new Object[]{"notALong", 1L};

        when(joinPoint.getArgs()).thenReturn(args);

        assertThrows(ClassCastException.class, () -> {
            followUserAspect.afterFollowUserMethod(joinPoint, null);
        });
    }

    @Test
    public void testAfterFollowUserMethod_ProducerThrowsException() {
        long followerId = 5L;
        long followeeId = 6L;
        Object[] args = new Object[]{followerId, followeeId};

        when(joinPoint.getArgs()).thenReturn(args);
        doThrow(new RuntimeException("Producer failure")).when(kafkaProducer).sendFollowUserEvent(followerId, followeeId);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            followUserAspect.afterFollowUserMethod(joinPoint, null);
        });

        assertEquals("Producer failure", exception.getMessage());
    }
}
