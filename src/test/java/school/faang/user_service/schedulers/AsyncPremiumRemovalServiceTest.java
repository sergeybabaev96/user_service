package school.faang.user_service.schedulers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AsyncPremiumRemovalServiceTest {

    @Mock
    private PremiumRepository premiumRepository;

    @InjectMocks
    private AsyncPremiumRemovalService asyncPremiumRemovalService;

    private List<Premium> batch;
    private final int BATCH_SIZE = 1000;

    @BeforeEach
    public void setUp() {
        batch = List.of(new Premium(), new Premium(), new Premium());
    }

    @Test
    public void testProcessBatch() {
        asyncPremiumRemovalService.processBatch(batch, BATCH_SIZE);

        verify(premiumRepository).deleteAll(batch);
    }
}
