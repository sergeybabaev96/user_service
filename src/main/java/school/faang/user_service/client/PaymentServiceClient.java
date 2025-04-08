package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;

@FeignClient(name = "services.payment-service", url = "${services.payment-service.host}:${services.payment-service.port}")
public interface PaymentServiceClient {

    @GetMapping("/api/payment")
    ResponseEntity<PaymentResponseDto> sendPayment(@RequestBody PaymentRequestDto paymentRequestDto);
}
