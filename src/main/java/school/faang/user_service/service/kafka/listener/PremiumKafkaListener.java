package school.faang.user_service.service.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.premium.PremiumPaymentResponseDto;
import school.faang.user_service.service.premium.PremiumService;

@Component
@Slf4j
@RequiredArgsConstructor
public class PremiumKafkaListener {
    private final PremiumService premiumService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${spring.kafka.consumer.topics.payment.premium.payment-response-topic}",
            groupId = "${spring.kafka.consumer.groups.premium.payment.payment-group}"
    )
    @Transactional
    public void premiumPaymentResponseListener(String message, Acknowledgment acknowledgment) {
        log.info("Received message from kafka: {}", message);
        PremiumPaymentResponseDto premiumPaymentResponse;
        try {
            premiumPaymentResponse = objectMapper.readValue(message, PremiumPaymentResponseDto.class);
        } catch (JsonProcessingException e) {
            log.error("Error while deserializing PremiumPaymentResponseDto", e);
            throw new RuntimeException(e);
        }

        premiumService.updatePremium(premiumPaymentResponse);

        try {
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to acknowledge Kafka message", e);
            throw new RuntimeException(e);
        }
    }
}
