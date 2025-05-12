package school.faang.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.outbox.OutboxMessage;
import school.faang.user_service.publisher.outbox.OutboxPublisher;
import school.faang.user_service.repository.outbox.OutboxRepository;
import school.faang.user_service.service.outbox.OutboxService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class OutboxServiceTest {

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private OutboxPublisher outboxPublisher;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OutboxService outboxService;

    @Captor
    private ArgumentCaptor<OutboxMessage> messageCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveEventSaveMessageToOutbox() throws JsonProcessingException {
        String eventType = "USER_CREATED";
        String payload = "{\"id\":1}";

        when(objectMapper.writeValueAsString(any())).thenReturn(payload);
        outboxService.saveEvent(eventType, payload);

        verify(outboxRepository).save(messageCaptor.capture());
        OutboxMessage savedMessage = messageCaptor.getValue();

        assertEquals(eventType, savedMessage.getEventType());
        assertEquals(payload, savedMessage.getPayload());
        assertNotNull(savedMessage.getEventId());
    }

    @Test
    void testProcessOutboxMarkAsPublished() {
        OutboxMessage message1 = OutboxMessage.builder()
                .id(1L)
                .eventId(UUID.randomUUID())
                .eventType("EVENT_1")
                .payload("{}")
                .createdAt(LocalDateTime.now())
                .build();

        OutboxMessage message2 = OutboxMessage.builder()
                .id(2L)
                .eventId(UUID.randomUUID())
                .eventType("EVENT_2")
                .payload("{}")
                .createdAt(LocalDateTime.now())
                .build();

        List<OutboxMessage> messages = List.of(message1, message2);
        when(outboxRepository.findAllByPublishedAtIsNullOrderByCreatedAtAsc()).thenReturn(messages);

        outboxService.processOutbox();

        verify(outboxPublisher).publish(messages);
        verify(outboxRepository).markAsPublished(
                eq(List.of(1L, 2L)),
                any(LocalDateTime.class)
        );
    }

    @Test
    void testProcessOutboxWhenNoMessages() {
        when(outboxRepository.findAllByPublishedAtIsNullOrderByCreatedAtAsc()).thenReturn(List.of());

        outboxService.processOutbox();

        verifyNoInteractions(outboxPublisher);
        verify(outboxRepository, never()).markAsPublished(any(), any());
    }

    @Test
    void testProcessOutboxWhenPublisherFails() {

        OutboxMessage message = OutboxMessage.builder()
                .id(1L)
                .eventId(UUID.randomUUID())
                .eventType("EVENT")
                .payload("{}")
                .createdAt(LocalDateTime.now())
                .build();

        List<OutboxMessage> messages = List.of(message);
        when(outboxRepository.findAllByPublishedAtIsNullOrderByCreatedAtAsc()).thenReturn(messages);
        doThrow(new RuntimeException("Publishing failed")).when(outboxPublisher).publish(messages);

        outboxService.processOutbox();

        verify(outboxPublisher).publish(messages);
        verify(outboxRepository, never()).markAsPublished(any(), any());
    }
}
