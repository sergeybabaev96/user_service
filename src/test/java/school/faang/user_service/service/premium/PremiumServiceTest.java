package school.faang.user_service.service.premium;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.payment_service.PaymentResponse;
import school.faang.user_service.dto.payment_service.PaymentStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.PremiumException;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.payment.PaymentService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {
    private static final Long USER_ID = 1L;
    private static final PremiumPeriod PREMIUM_PERIOD = PremiumPeriod.THREE_MONTHS;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private UserService userService;

    @InjectMocks
    PremiumService premiumService;

    private int premiumPeriodDays;
    private final LocalDateTime currentTime = LocalDateTime.now();
    private LocalDateTime futureTime;
    private User testUser;
    Premium testPremium;
    List<Premium> testBatch = List.of(new Premium());

    @BeforeEach
    void setUp() {
        premiumPeriodDays = PREMIUM_PERIOD.getDays();
        futureTime = currentTime.plusDays(1L);

        testUser = User.builder().id(USER_ID).build();
        testPremium = Premium.builder()
                .endDate(futureTime)
                .build();
    }

    @Test
    void buyPremiumUserHavePremiumThrowsException() {
        when(premiumRepository.findByUserIdAndEndDateAfter(eq(USER_ID), any(LocalDateTime.class)))
                .thenReturn(Optional.of(testPremium));

        DataValidationException ex = assertThrows(DataValidationException.class, () ->
                premiumService.buyPremium(USER_ID, premiumPeriodDays));

        assertEquals("User 1 already has a valid premium subscription until " + futureTime, ex.getMessage());
        verify(premiumRepository, times(1)).findByUserIdAndEndDateAfter(any(), any());
        verify(paymentService, never()).sendPaymentRequest(any());
    }

    @Test
    void buyPremiumResponseStatusNotSuccessThrowsException() {
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .status(PaymentStatus.FAIL)
                .build();
        when(premiumRepository.findByUserIdAndEndDateAfter(any(), any()))
                .thenReturn(Optional.empty());
        when(paymentService.sendPaymentRequest(PREMIUM_PERIOD)).thenReturn(paymentResponse);

        PremiumException ex = assertThrows(PremiumException.class, () ->
                premiumService.buyPremium(USER_ID, premiumPeriodDays));

        assertEquals("Cannot buy premium, try again later", ex.getMessage());
        verify(premiumRepository, times(1)).findByUserIdAndEndDateAfter(any(), any());
        verify(paymentService, times(1)).sendPaymentRequest(any());
        verify(userService, never()).getUser(USER_ID);
    }

    @Test
    void buyPremiumSuccess() {
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .status(PaymentStatus.SUCCESS)
                .build();
        Premium expected = Premium.builder()
                .user(testUser)
                .startDate(currentTime)
                .endDate(currentTime.plusDays(premiumPeriodDays))
                .build();

        when(premiumRepository.findByUserIdAndEndDateAfter(any(), any()))
                .thenReturn(Optional.empty());
        when(paymentService.sendPaymentRequest(PREMIUM_PERIOD)).thenReturn(paymentResponse);
        when(userService.getUser(USER_ID)).thenReturn(testUser);
        when(premiumRepository.save(any())).thenReturn(expected);

        Premium actual = premiumService.buyPremium(USER_ID, premiumPeriodDays);

        assertEquals(expected.getUser().getId(), actual.getUser().getId());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());

        verify(premiumRepository, times(1)).findByUserIdAndEndDateAfter(any(), any());
        verify(premiumRepository, times(1)).save(any());
        verify(paymentService, times(1)).sendPaymentRequest(any());
        verify(userService, times(1)).getUser(USER_ID);
    }

    @Test
    public void testDefineExpiredPremiumIsSuccessful() {
        premiumService.defineExpirePremium();

        verify(premiumRepository, times(1)).findAllByEndDateBefore(any(LocalDateTime.class));
    }

    @Test
    public void testRemovePremiumIsSuccessful() {
        premiumService.removePremium(testBatch);

        verify(premiumRepository, times(1)).deleteAll(testBatch);
    }

}
