package school.faang.user_service.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.premium.PremiumService;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ScheduledPremiumRemoverTest {
    @Mock
    private PremiumService premiumService;

    @InjectMocks
    private ScheduledPremiumRemover premiumRemover;

    @Test
    @DisplayName("Правильно вызывается метод удаления просроченной подписки")
    public void removeExpiredPremium() {
        premiumRemover.removeExpiredPremium();

        verify(premiumService, times(1)).removeExpiredPremium(anyInt());
    }
}
