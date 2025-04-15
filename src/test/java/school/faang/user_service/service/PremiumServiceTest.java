package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.premium.PremiumAsyncRemover;
import school.faang.user_service.service.premium.PremiumService;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {
    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private PremiumAsyncRemover premiumAsyncRemover;

    @InjectMocks
    private PremiumService premiumService;

    @Test
    @DisplayName("Успешное удаление премиумов")
    public void testRemoveExpiredPremium() {
        int batchSize = 1;
        List<Premium> expiredPremiums = List.of(new Premium(), new Premium());
        when(premiumRepository.findAllByEndDateBefore(any())).thenReturn(expiredPremiums);

        premiumService.removeExpiredPremium(batchSize);

        verify(premiumAsyncRemover, times(2)).removeBatchAsync(any());
    }

    @Test
    @DisplayName("Ничего не происходит, если нет истекших премиумов")
    public void testRemoveExpiredPremiumNoExpiredPremiums() {
        int batchSize = 1;
        when(premiumRepository.findAllByEndDateBefore(any())).thenReturn(Collections.emptyList());

        premiumService.removeExpiredPremium(batchSize);

        verify(premiumAsyncRemover, never()).removeBatchAsync(any());
        verify(premiumRepository, times(1)).findAllByEndDateBefore(any());
    }
}
