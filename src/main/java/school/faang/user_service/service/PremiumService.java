package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.payment.CreateOrderDto;
import school.faang.user_service.dto.payment.OrderDto;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumPlan;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final UserContext userContext;

    public OrderDto buyPremium(long user_id, PremiumPlan plan, String paymentMethod) {
        if (!userRepository.existsById(user_id)) {
            throw new EntityNotFoundException("Такой пользователь не существует");
        }
        if (premiumRepository.existsByUserId(user_id)) {
            throw new DataValidationException("Пользователь уже является премиум пользователем");
        }
        CreateOrderDto dto = new CreateOrderDto(
                "premium",
                plan.toString(),
                paymentMethod,
                user_id
        );

        return paymentServiceClient.createOrder(dto);
    }

    public void activatePremiumForUser(Long orderId) {
        userContext.setUserId(0);
        OrderDto orderDto = paymentServiceClient.getOrder(orderId);
        if (!orderDto.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
            throw new BusinessException("Заказ не оплачен");
        }
        if (!orderDto.getServiceType().equalsIgnoreCase("premium")) {
            throw new BusinessException("Тип услуги не корректен");
        }
        var user = userRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        long daysToEnd = PremiumPlan.valueOf(orderDto.getServicePlan().toUpperCase()).getDays();
        var premium = Premium.builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(daysToEnd))
                .build();
        premiumRepository.save(premium);
    }
}
