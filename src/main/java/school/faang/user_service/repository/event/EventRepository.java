package school.faang.user_service.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

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

    default Event findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new NoSuchElementException(
                String.format("Event id %d not found", id)));
    }


    @Query(nativeQuery = true, value = """
            SELECT e.* FROM event e
            WHERE e.end_date < :endDate
            """)
    List<Event> findAllByEndDateBefore(LocalDateTime endDate);

}