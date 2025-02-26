package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.RemoveExpiredPremiumJobProperties;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.premium.impl.PremiumDeletionService;
import school.faang.user_service.service.premium.impl.PremiumServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceDeleteExpiredTest {
    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private PremiumDeletionService premiumDeletionService;

    @Spy
    private RemoveExpiredPremiumJobProperties removeExpiredPremiumJobProperties;

    @InjectMocks
    private PremiumServiceImpl premiumService;

    private List<Premium> expiredPremiums;

    @BeforeEach
    void setUp() {
        removeExpiredPremiumJobProperties.setBatchSize(2);
        removeExpiredPremiumJobProperties.setThreadPoolSize(2);
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
        doNothing().when(premiumDeletionService).deletePremiumsInBatch(anyList());

        premiumService.deleteExpiredPremiumsAsync();

        verify(premiumRepository, times(1)).findAllByEndDateBefore(any());
        // 5 премиумов, батчи по 2, значит 3 вызова
        verify(premiumDeletionService, times(3)).deletePremiumsInBatch(anyList());
    }

    @Test
    void deleteExpiredPremiumsAsync_NoExpiredPremiums_LogsMessage() {
        when(premiumRepository.findAllByEndDateBefore(any())).thenReturn(new ArrayList<>());

        premiumService.deleteExpiredPremiumsAsync();

        verify(premiumRepository, times(1)).findAllByEndDateBefore(any());
        verify(premiumDeletionService, never()).deletePremiumsInBatch(anyList());
    }

    @Test
    void deletePremiumsInBatch_WithValidBatch_Success() {
        List<Premium> batch = expiredPremiums.subList(0, 2);
        doNothing().when(premiumDeletionService).deletePremiumsInBatch(anyList());

        premiumDeletionService.deletePremiumsInBatch(batch);

        verify(premiumDeletionService, times(1)).deletePremiumsInBatch(anyList());
    }
}