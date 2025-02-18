package school.faang.user_service.repository.mentorship;





import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;

import java.util.List;

public interface MentorshipRepository extends CrudRepository<User, Long> {

    @Query(value = "SELECT mentee_id FROM mentorship WHERE mentor_id = :mentorId", nativeQuery = true)
    List<Long> findMenteeIdsByMentorId(@Param("mentorId") Long mentorId);


    @Query(value = "SELECT mentor_id FROM mentorship WHERE mentee_id = :menteeId", nativeQuery = true)
    List<Long> findMentorIdsByMenteeId(@Param("menteeId") Long menteeId);

    @Modifying
    @Query(value = "DELETE FROM mentorship WHERE mentor_id = :mentorId AND mentee_id = :menteeId", nativeQuery = true)
    void deleteByMentorIdAndMenteeId(@Param("mentorId") Long mentorId, @Param("menteeId") Long menteeId);

    @Modifying
    @Query(value = "DELETE FROM mentorship WHERE mentee_id = :menteeId AND mentor_id = :mentorId", nativeQuery = true)
    void deleteByMenteeIdAndMentorId(@Param("menteeId") Long menteeId, @Param("mentorId") Long mentorId);
}
