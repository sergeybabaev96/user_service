package school.faang.user_service.service.premium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PremiumBatchServiceTest {

    @Mock
    private PremiumRepository premiumRepository;

    @InjectMocks
    private PremiumBatchService premiumBatchService;

    @Test
    public void testRemoveBatch() {
        doNothing().when(premiumRepository).deleteByIdIn(anyList());

        premiumBatchService.removeBatch(List.of(1L, 2L), LocalDateTime.now());

        verify(premiumRepository, times(1)).deleteByIdIn(anyList());
    }
}
