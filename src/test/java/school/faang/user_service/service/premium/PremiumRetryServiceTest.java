package school.faang.user_service.service.premium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
        Premium premium1 = new Premium();
        premium1.setId(1L);
        Premium premium2 = new Premium();
        premium2.setId(2L);

        when(premiumRepository.findAllByEndDateBefore(any())).thenReturn(List.of(premium1, premium2));

        List<Long> result = premiumRetryService.getExpiredPremiumIds();

        assertEquals(List.of(1L, 2L), result);
        verify(premiumRepository, times(1)).findAllByEndDateBefore(any());
    }

}