package school.faang.user_service.repository.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.outbox.OutboxMessage;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {

    List<OutboxMessage> findAllByPublishedAtIsNullOrderByCreatedAtAsc();

    @Modifying
    @Query("UPDATE OutboxMessage m SET m.publishedAt = :publishedAt WHERE m.id IN :ids")
    void markAsPublished(List<Long> ids, LocalDateTime publishedAt);
}
