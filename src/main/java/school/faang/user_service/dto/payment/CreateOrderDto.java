package school.faang.user_service.dto.payment;

public record CreateOrderDto(
        String serviceType,
        String plan,
        String paymentMethod,
        Long userId
) {
}
