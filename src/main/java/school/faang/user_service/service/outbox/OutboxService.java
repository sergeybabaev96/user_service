package school.faang.user_service.service.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.outbox.OutboxMessage;
import school.faang.user_service.publisher.outbox.OutboxPublisher;
import school.faang.user_service.repository.outbox.OutboxRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final OutboxPublisher outboxPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public void saveEvent(String eventType, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxMessage message = OutboxMessage.builder()
                    .eventId(UUID.randomUUID())
                    .eventType(eventType)
                    .payload(payload)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxRepository.save(message);
            log.info("Event saved to outbox: {}", message);

        } catch (Exception e) {
            log.error("Error serializing event to JSON", e);
            throw new RuntimeException("Error serializing event to JSON", e);
        }
    }

    @Scheduled(fixedDelayString = "${outbox.publishing.interval:5000}")
    @Transactional
    public void processOutbox() {
        List<OutboxMessage> messages = outboxRepository.findAllByPublishedAtIsNullOrderByCreatedAtAsc();
        if (messages.isEmpty()) {
            return;
        }

        try {
            outboxPublisher.publish(messages);
            List<Long> publishedIds = messages.stream().map(OutboxMessage::getId).toList();
            outboxRepository.markAsPublished(publishedIds, LocalDateTime.now());
            log.info("Published {} outbox messages", messages.size());
        } catch (Exception e) {
            log.error("Failed to publish outbox messages", e);
        }
    }
}
