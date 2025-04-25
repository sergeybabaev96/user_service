package school.faang.user_service.service.premium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumRetryServiceTest {

    @Mock
    private PremiumRepository premiumRepository;

    @InjectMocks
    private PremiumRetryService premiumRetryService;

    @Test
    void getExpiredPremiumIdsSucceed() {
        LocalDateTime now = premiumRetryService.getNow();

        when(premiumRepository.findIdsByEndDateBefore(now))
                .thenReturn(List.of(1L, 2L));

        List<Long> result = premiumRetryService.getExpiredPremiumIds();

        assertEquals(List.of(1L, 2L), result);
        verify(premiumRepository, times(1)).findIdsByEndDateBefore(now);
    }

}