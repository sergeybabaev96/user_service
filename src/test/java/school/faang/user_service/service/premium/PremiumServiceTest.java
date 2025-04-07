package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {

    @InjectMocks
    private PremiumService premiumService;

    @Mock
    private PremiumRepository premiumRepository;

    @Value("${user-premium.partition-size}")
    private int partitionSize = 10;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(premiumService, "partitionSize", partitionSize);
    }

    @Test
    void testRemoveExpiredPremiumAccessWhenListEmpty() {
        when(premiumRepository.findAllByEndDateBefore(any())).thenReturn(Collections.emptyList());
        premiumService.removeExpiredPremiumAccess();

        verify(premiumRepository, times(0)).deleteByIdIn(any());
    }

    @Test
    void testRemoveExpiredPremiumAccessSuccess() {
        LocalDateTime now = LocalDateTime.now();
        Premium premium1 = new Premium(1, new User(), now.minusDays(1), now.minusHours(1));
        List<Premium> expiredPremiums = List.of(premium1);

        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class))).thenReturn(expiredPremiums);
        doNothing().when(premiumRepository).deleteByIdIn(anyList());

        premiumService.removeExpiredPremiumAccess();

        verify(premiumRepository, times(1)).deleteByIdIn(any());
    }

    @Test
    void testRemoveExpiredPremiumAccessWhenException() {
        when(premiumRepository.findAllByEndDateBefore(any())).thenThrow(new RuntimeException("Error"));

        premiumService.removeExpiredPremiumAccess();

    }
}
