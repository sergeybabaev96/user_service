package school.faang.user_service.service.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.exchange.ExchangeResponseDto;
import school.faang.user_service.dto.premium.PremiumPaymentResponseDto;
import school.faang.user_service.service.premium.PremiumServiceImpl;

import static school.faang.user_service.messages.ErrorMessages.FAILED_TO_ACKNOWLEDGE_KAFKA_MESSAGE;

@Component
@Slf4j
@RequiredArgsConstructor
public class PremiumKafkaListener {
    public static final String RECEIVED_MESSAGE_FROM_KAFKA = "Received message from kafka: {}";

    private final PremiumServiceImpl premiumService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${spring.kafka.consumer.topics.premium.payment.payment-response-topic}",
            groupId = "${spring.kafka.consumer.groups.premium.payment.payment-group}"
    )
    @Transactional
    public void premiumPaymentResponseListener(String message, Acknowledgment acknowledgment) {
        log.info(RECEIVED_MESSAGE_FROM_KAFKA, message);
        PremiumPaymentResponseDto premiumPaymentResponse;
        try {
            premiumPaymentResponse = objectMapper.readValue(message, PremiumPaymentResponseDto.class);
        } catch (JsonProcessingException e) {
            log.error("Error while deserializing PremiumPaymentResponseDto", e);
            throw new RuntimeException(e);
        }

        premiumService.updatePremium(premiumPaymentResponse);
        acknowledgeMessage(acknowledgment);
    }

    @KafkaListener(
            topics = "${spring.kafka.consumer.topics.premium.payment.price-response-topic}",
            groupId = "${spring.kafka.consumer.groups.premium.payment.price-group}"
    )
    @Transactional
    public void premiumPriceResponseListener(String message, Acknowledgment acknowledgment) {
        log.info(RECEIVED_MESSAGE_FROM_KAFKA, message);
        ExchangeResponseDto exchangeResponse;
        try {
            exchangeResponse = objectMapper.readValue(message, ExchangeResponseDto.class);
        } catch (JsonProcessingException e) {
            log.error("Error while deserializing ExchangeResponseDto", e);
            throw new RuntimeException(e);
        }

        premiumService.fetchPremiumPrice(exchangeResponse);
        acknowledgeMessage(acknowledgment);
    }

    private void acknowledgeMessage(Acknowledgment acknowledgment) {
        try {
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error(FAILED_TO_ACKNOWLEDGE_KAFKA_MESSAGE, e);
            throw new RuntimeException(e);
        }
    }
}
