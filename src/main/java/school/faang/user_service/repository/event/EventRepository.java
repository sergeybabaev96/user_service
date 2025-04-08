package school.faang.user_service.repository.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;

import java.util.Collection;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(nativeQuery = true, value = """
            SELECT e.* FROM event e
            WHERE e.user_id = :userId
            """)
    List<Event> findAllByUserId(long userId);

    @Query(nativeQuery = true, value = """
            SELECT e.* FROM event e
            JOIN user_event ue ON ue.event_id = e.id
            WHERE ue.user_id = :userId
            """)
    List<Event> findParticipatedEventsByUserId(long userId);

    @Query("SELECT e FROM Event e WHERE e.status IN :statuses")
    Page<Event> findPastEventsWithPagination(
            Pageable pageable, @Param("statuses") Collection<EventStatus> statuses);

    @Query("SELECT COUNT(*) FROM Event e WHERE e.status IN :statuses")
    long countByStatusIn(@Param("statuses") Collection<EventStatus> statuses);
}