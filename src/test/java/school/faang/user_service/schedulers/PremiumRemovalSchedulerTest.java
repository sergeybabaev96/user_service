package school.faang.user_service.schedulers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.PremiumService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PremiumRemovalSchedulerTest {

    @Mock
    private PremiumService premiumService;

    @InjectMocks
    private PremiumRemovalScheduler premiumRemovalScheduler;

    @Test
    public void testRemovePremium() {
        premiumRemovalScheduler.removePremium();
        verify(premiumService).deleteExpiredPremiums();
    }
}
