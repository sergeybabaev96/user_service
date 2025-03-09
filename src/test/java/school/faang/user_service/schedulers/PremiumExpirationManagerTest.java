package school.faang.user_service.schedulers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PremiumExpirationManagerTest {

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private AsyncPremiumRemovalService asyncPremiumRemovalService;

    @InjectMocks
    private PremiumExpirationManager premiumExpirationManager;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(premiumExpirationManager, "batchSize", 1000);
    }

    @Test
    public void testDeleteExpiredPremiums() {
        Page<Premium> page = mock(Page.class);
        when(page.getContent()).thenReturn(Collections.emptyList());
        when(page.hasNext()).thenReturn(false);
        when(premiumRepository.findByEndDateBefore(any(), any())).thenReturn(page);

        premiumExpirationManager.deleteExpiredPremiums();

        verify(premiumRepository).findByEndDateBefore(any(), any());
        verify(asyncPremiumRemovalService).processBatch(anyList(), anyInt());
    }
}
