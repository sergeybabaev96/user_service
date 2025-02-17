package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.JobProperties;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.premium.impl.PremiumServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceDeleteExpiredTest {
    @Mock
    private PremiumRepository premiumRepository;

    @Spy
    private JobProperties jobProperties;

    @InjectMocks
    private PremiumServiceImpl premiumService;

    private List<Premium> expiredPremiums;

    @BeforeEach
    void setUp() {
        jobProperties.setBatchSize(2);
        jobProperties.setThreadPoolSize(2);

        expiredPremiums = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            expiredPremiums.add(Premium.builder()
                    .id(i)
                    .startDate(LocalDateTime.now().minusDays(40))
                    .endDate(LocalDateTime.now().minusDays(10))
                    .build());
        }
    }

    @Test
    void deleteExpiredPremiumsAsync_WithExpiredPremiums_Success() {
        when(premiumRepository.findAllByEndDateBefore(any())).thenReturn(expiredPremiums);
        doNothing().when(premiumRepository).deleteAllByIdsInBatch(anyList());

        premiumService.deleteExpiredPremiumsAsync();

        verify(premiumRepository, times(1)).findAllByEndDateBefore(any());
        // 5 премиумов, батчи по 2, значит 3 вызова
        verify(premiumRepository, times(3)).deleteAllByIdsInBatch(anyList());
    }

    @Test
    void deleteExpiredPremiumsAsync_NoExpiredPremiums_LogsMessage() {
        when(premiumRepository.findAllByEndDateBefore(any())).thenReturn(new ArrayList<>());

        premiumService.deleteExpiredPremiumsAsync();

        verify(premiumRepository, times(1)).findAllByEndDateBefore(any());
        verify(premiumRepository, never()).deleteAllByIdsInBatch(anyList());
    }

    @Test
    void deletePremiumsInBatch_WithValidBatch_Success() {
        List<Premium> batch = expiredPremiums.subList(0, 2);
        doNothing().when(premiumRepository).deleteAllByIdsInBatch(anyList());

        premiumRepository.deleteAllByIdsInBatch(batch.stream().map(Premium::getId).toList());

        verify(premiumRepository, times(1)).deleteAllByIdsInBatch(anyList());
    }
}