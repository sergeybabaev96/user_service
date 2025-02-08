package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.premium.PaymentResponse;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.enums.PaymentStatus;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.exception.PaymentPayException;
import school.faang.user_service.exception.PaymentServiceException;
import school.faang.user_service.exception.PremiumAlreadyExistsException;
import school.faang.user_service.mapper.PremiumMapperImpl;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.PremiumService;
import school.faang.user_service.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {

    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Spy
    private PremiumMapperImpl premiumMapper;
    @Mock
    private UserService userService;

    @InjectMocks
    private PremiumService premiumService;

    private User user;
    private Premium premium;
    private long userId = 1L;


    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(userId)
                .build();

        premium = Premium.builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(90))
                .build();
    }

    @Test
    public void buyPremium_Success() {

        when(userService.getUserById(userId)).thenReturn(user);
        when(premiumRepository.existsByUserId(userId)).thenReturn(false);
        when(paymentServiceClient.processPayment(any())).thenReturn(new PaymentResponse(
                PaymentStatus.SUCCESS, 6345, 1L,
                new BigDecimal(30), Currency.getInstance("USD"), "success"
        ));
        when(premiumRepository.save(any())).thenReturn(premium);

        var premiumPeriod = PremiumPeriod.fromDays(90);

        PremiumDto premiumDto = premiumService.buyPremium(userId, premiumPeriod);

        assertEquals(premiumDto.userName(), premium.getUser().getUsername());
    }

    @Test
    void buyPremium_AlreadyExists() {

        when(premiumRepository.existsByUserId(userId)).thenReturn(true);

        PremiumAlreadyExistsException exception = assertThrows(PremiumAlreadyExistsException.class, () ->
                premiumService.buyPremium(userId, PremiumPeriod.fromDays(90))
        );

        assertEquals("The user is already available in the premium", exception.getMessage());

        verify(premiumRepository, times(1)).existsByUserId(userId);
        verify(premiumMapper, never()).toEntity(any());
        verify(premiumRepository, never()).save(any());
        verify(premiumMapper, never()).toDto(any());
    }

    @Test
    void buyPremium_PaymentPayFailed() {

        when(userService.getUserById(userId)).thenReturn(user);
        when(premiumRepository.existsByUserId(userId)).thenReturn(false);
        when(paymentServiceClient.processPayment(any())).thenReturn(new PaymentResponse(
                PaymentStatus.ERROR, 6345, 1L,
                new BigDecimal(25), Currency.getInstance("USD"), "success"
        ));


        PaymentPayException exception = assertThrows(PaymentPayException.class, () ->
                premiumService.buyPremium(userId, PremiumPeriod.fromDays(90))
        );

        assertEquals("Payment failed.", exception.getMessage());

        verify(premiumRepository, times(1)).existsByUserId(userId);
        verify(premiumMapper, never()).toEntity(any());
        verify(premiumRepository, never()).save(any());
        verify(premiumMapper, never()).toDto(any());
    }

    @Test
    void buyPremium_PaymentServiceFailed() {

        when(userService.getUserById(userId)).thenReturn(user);
        when(premiumRepository.existsByUserId(userId)).thenReturn(false);
        when(paymentServiceClient.processPayment(any())).thenThrow(new PaymentServiceException("Payment service not working."));

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
                premiumService.buyPremium(userId, PremiumPeriod.fromDays(90))
        );

        assertEquals("Payment service not working.", exception.getMessage());

        verify(premiumRepository, times(1)).existsByUserId(userId);
        verify(premiumMapper, never()).toEntity(any());
        verify(premiumRepository, never()).save(any());
        verify(premiumMapper, never()).toDto(any());
    }


}