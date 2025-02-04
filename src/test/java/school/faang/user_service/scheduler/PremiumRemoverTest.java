package school.faang.user_service.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.premium.PremiumService;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PremiumRemoverTest {

    @Mock
    private PremiumService premiumService;

    @InjectMocks
    private PremiumRemover premiumRemover;

    @Test
    public void testRemoveExpiredPremium() {
        doNothing().when(premiumService).removeAllExpiredPremiumAccess();

        premiumRemover.removePremium();

        verify(premiumService).removeAllExpiredPremiumAccess();
    }
}