package school.faang.user_service.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
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

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM event WHERE id IN (:ids)", nativeQuery = true)
    void deleteByIds(@Param("ids") List<Long> batch);

    @Query(value = "SELECT id FROM event WHERE end_date IS NOT NULL AND end_date < :now", nativeQuery = true)
    List<Long> findIdsByEndDateBefore(@Param("now") LocalDateTime now);
}