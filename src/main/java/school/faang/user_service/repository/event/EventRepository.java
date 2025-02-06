package school.faang.user_service.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.event.Event;

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

    @Query(nativeQuery = true, value = """
            SELECT e.*
            FROM event e
                LEFT JOIN tariff t ON t.event_id = e.id
            ORDER BY
                CASE WHEN t.active = true THEN t.priority END
            LIMIT ?1 OFFSET ?2
    """)
    List<Event> findAllOrderByTariffAndLimit(Integer limit, Integer offset);
}