package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.payment.CreateOrderDto;
import school.faang.user_service.dto.payment.OrderDto;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumPlan;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {

    @InjectMocks
    private PremiumService premiumService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private UserContext userContext;

    private final long USER_ID = 1L;
    private final long ORDER_ID = 1L;

    private OrderDto orderDto;

    @BeforeEach
    public void setUp() {
        reset(userContext);
        orderDto = OrderDto.builder()
                .userId(USER_ID)
                .serviceType("premium")
                .servicePlan("month")
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();
    }

    @Test
    public void testBuyPremium_UserNotFound() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                premiumService.buyPremium(USER_ID, PremiumPlan.MONTH, "credit_card")
        );
    }

    @Test
    public void testBuyPremium_UserAlreadyPremium() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(premiumRepository.existsByUserId(USER_ID)).thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                premiumService.buyPremium(USER_ID, PremiumPlan.MONTH, "credit_card")
        );

        assertEquals("Пользователь уже является премиум пользователем", exception.getMessage());
    }

    @Test
    public void testBuyPremium_Success() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(premiumRepository.existsByUserId(USER_ID)).thenReturn(false);
        ArgumentCaptor<CreateOrderDto> argumentCaptor = ArgumentCaptor.forClass(CreateOrderDto.class);

        when(paymentServiceClient.createOrder(any(CreateOrderDto.class))).thenReturn(new OrderDto());

        premiumService.buyPremium(USER_ID, PremiumPlan.MONTH, "credit_card");

        verify(paymentServiceClient, atLeastOnce()).createOrder(argumentCaptor.capture());
        CreateOrderDto dto = argumentCaptor.getValue();
        assertEquals("premium", dto.serviceType());
        assertEquals(PremiumPlan.MONTH.toString(), dto.plan());
        assertEquals("credit_card", dto.paymentMethod());
    }

    @Test
    public void testActivatePremiumForUser_OrderNotPaid() {
        orderDto.setPaymentStatus(PaymentStatus.PENDING);
        when(paymentServiceClient.getOrder(ORDER_ID)).thenReturn(orderDto);

        assertThrows(BusinessException.class, () ->
                premiumService.activatePremiumForUser(ORDER_ID)
        );
    }

    @Test
    public void testActivatePremiumForUser_InvalidServiceType() {
        orderDto.setServiceType("promotion");
        when(paymentServiceClient.getOrder(ORDER_ID)).thenReturn(orderDto);

        assertThrows(BusinessException.class, () ->
                premiumService.activatePremiumForUser(ORDER_ID)
        );
    }

    @Test
    public void testActivatePremiumForUser_UserNotFound() {
        when(paymentServiceClient.getOrder(ORDER_ID)).thenReturn(orderDto);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                premiumService.activatePremiumForUser(ORDER_ID)
        );
    }

    @Test
    public void testActivatePremiumForUser_Success() {
        User user = new User();
        ArgumentCaptor<Premium> argumentCaptor = ArgumentCaptor.forClass(Premium.class);
        when(paymentServiceClient.getOrder(ORDER_ID)).thenReturn(orderDto);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        premiumService.activatePremiumForUser(ORDER_ID);

        verify(premiumRepository, atLeastOnce()).save(argumentCaptor.capture());
        Premium premium = argumentCaptor.getValue();
        assertEquals(user, premium.getUser());
        assertEquals(LocalDateTime.now().getDayOfMonth(), premium.getStartDate().getDayOfMonth());
        assertEquals(
                LocalDateTime.now().plusDays(PremiumPlan.MONTH.getDays()).getDayOfMonth(),
                premium.getEndDate().getDayOfMonth()
        );
    }
}