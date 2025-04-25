package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {

    @InjectMocks
    private PremiumService premiumService;

    @Mock
    private PremiumBatchService premiumBatchService;
    @Mock
    private PremiumRetryService premiumRetryService;

    @Value("${user-premium.partition-size}")
    private int partitionSize = 2;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        ReflectionTestUtils.setField(premiumService, "partitionSize", partitionSize);
    }

    @Test
    @DisplayName("test removeExpiredPremiumAccess when method deleteByIdIn called")
    void testRemoveExpiredPremiumAccess() {
        List<Long> expiredIds = List.of(1L, 2L, 3L);
        when(premiumRetryService.getExpiredPremiumIds()).thenReturn(expiredIds);
        when(premiumRetryService.getNow()).thenReturn(now);

        premiumService.removeExpiredPremiumAccess();

        verify(premiumBatchService, times(2)).removeBatch(anyList(), any(LocalDateTime.class));
    }
}
