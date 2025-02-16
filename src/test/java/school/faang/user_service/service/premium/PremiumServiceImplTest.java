package school.faang.user_service.service.premium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.properties.PremiumSchedulerProperties;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumServiceImplTest {

    @Mock
    private PremiumSchedulerProperties properties;

    @Mock
    private PremiumRepository premiumRepository;

    @InjectMocks
    private PremiumServiceImpl premiumService;

    @Test
    public void testRemoveExpiredPremiums() {
        List<Premium> expiredPremiums = List.of(getExpiredPremium());
        when(properties.getBatch()).thenReturn(5);
        when(premiumRepository.findAllByEndDateBefore(any())).thenReturn(expiredPremiums);

        premiumService.removeAllExpiredPremiumAccess();

        verify(premiumRepository).deleteAll(expiredPremiums);
    }

    private Premium getExpiredPremium() {
        return Premium.builder()
                .user(User.builder().id(1L).build())
                .endDate(getEndDate())
                .build();
    }

    private LocalDateTime getEndDate() {
        return LocalDateTime.of(2025, 2, 5, 0, 0);
    }
}