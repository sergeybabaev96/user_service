package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.service.PremiumService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumControllerTest {
    private final static Integer VALID_PREMIUM_PERIOD_DAYS = 30;

    @Mock
    private PremiumService premiumService;

    @InjectMocks
    private PremiumController premiumController;

    @Test
    void buyPremium_ValidRequest_ReturnsPremiumDto() {
        PremiumDto expectedPremiumDto = createTestPremiumDto();

        when(premiumService.buyPremium(VALID_PREMIUM_PERIOD_DAYS)).thenReturn(expectedPremiumDto);

        PremiumDto result = premiumController.buyPremium(VALID_PREMIUM_PERIOD_DAYS);

        assertEquals(expectedPremiumDto, result);
        assertNotNull(result);
        verify(premiumService).buyPremium(VALID_PREMIUM_PERIOD_DAYS);
    }

    private PremiumDto createTestPremiumDto() {
        return PremiumDto.builder()
                .id(1L)
                .userId(1L)
                .startDate(null)
                .endDate(null)
                .build();
    }
}
