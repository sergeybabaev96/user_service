package school.faang.user_service.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.common.PaymentStatus;
import school.faang.user_service.common.PremiumPeriod;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.PaymentRequest;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.dto.response.PaymentResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.PremiumInvalidDataException;
import school.faang.user_service.exception.PremiumNotFoundException;
import school.faang.user_service.mapper.PremiumMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.impl.PremiumServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {
    private static final Integer VALID_PREMIUM_PERIOD_DAYS = 30;
    private static final Long TEST_USER_ID = 1L;
    private static final Boolean USER_IS_ALREADY_PREMIUM_FLAG = true;
    private static final Boolean USER_IS_NOT_PREMIUM_FLAG = false;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private PremiumRepository premiumRepository;

    @Spy
    private PremiumMapperImpl premiumMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PremiumServiceImpl premiumService;

    @Mock
    private UserContext userContext;

    @Captor
    private ArgumentCaptor<PaymentRequest> paymentRequestCaptor;

    @Captor
    private ArgumentCaptor<Premium> premiumCaptor;

    private User testUser;
    private PremiumPeriod premiumPeriod;
    private Premium testPremium;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(TEST_USER_ID)
                .createdAt(LocalDateTime.now())
                .build();
        premiumPeriod = PremiumPeriod.fromDays(VALID_PREMIUM_PERIOD_DAYS);

        LocalDateTime now = LocalDateTime.now();
        testPremium = Premium.builder()
                .user(testUser)
                .startDate(now)
                .endDate(now.plusDays(premiumPeriod.getDays()))
                .build();
    }

    @Test
    void buyPremium_ValidRequest_Success() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(premiumRepository.existsByUserId(TEST_USER_ID)).thenReturn(USER_IS_NOT_PREMIUM_FLAG);
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class)))
                .thenReturn(getPaymentResponseEntity());
        when(premiumRepository.save(any(Premium.class))).thenReturn(testPremium);
        when(userContext.getUserId()).thenReturn(TEST_USER_ID);

        PremiumDto result = premiumService.buyPremium(VALID_PREMIUM_PERIOD_DAYS);

        assertNotNull(result);
        assertEquals(TEST_USER_ID, result.getUserId());

        verify(userRepository).findById(TEST_USER_ID);
        verify(premiumRepository).existsByUserId(TEST_USER_ID);
        verify(paymentServiceClient).sendPayment(paymentRequestCaptor.capture());
        verify(premiumRepository).save(premiumCaptor.capture());
        verify(premiumMapper).toDto(any(Premium.class));

        PaymentRequest capturedPaymentRequest = paymentRequestCaptor.getValue();
        assertEquals(premiumPeriod.getPrice(), capturedPaymentRequest.amount());
        assertEquals(premiumPeriod.getCurrency(), capturedPaymentRequest.currency());

        Premium capturedPremium = premiumCaptor.getValue();
        assertEquals(testUser, capturedPremium.getUser());
        assertNotNull(capturedPremium.getStartDate());
        assertNotNull(capturedPremium.getEndDate());
    }


    @Test
    void buyPremium_AlreadyPremiumUser_ThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(premiumRepository.existsByUserId(anyLong())).thenReturn(USER_IS_ALREADY_PREMIUM_FLAG);

        assertThrows(
                PremiumInvalidDataException.class,
                () -> premiumService.buyPremium(VALID_PREMIUM_PERIOD_DAYS)
        );

        verify(userRepository).findById(anyLong());
        verify(premiumRepository).existsByUserId(anyLong());
    }

    @Test
    void buyPremium_NoUserFound_ThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                PremiumNotFoundException.class,
                () -> premiumService.buyPremium(VALID_PREMIUM_PERIOD_DAYS)
        );

        verify(userRepository).findById(anyLong());
        verifyNoInteractions(premiumRepository);
    }

    private @NotNull ResponseEntity<PaymentResponse> getPaymentResponseEntity() {
        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                1,
                0L,
                premiumPeriod.getPrice(),
                premiumPeriod.getCurrency(),
                "OK"
        ));
    }
}