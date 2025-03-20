package school.faang.user_service.repository.mentorship;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.User;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface MentorshipRepository extends CrudRepository<User, Long> {
    @Query("SELECT u FROM User u JOIN u.mentors m WHERE m.id = :mentorId")
    List<User> findAllMenteesByMentorId(@Param("mentorId") Long mentorId);

    @Query("SELECT m FROM User m JOIN m.mentees u WHERE u.id = :menteeId")
    List<User> findAllMentorsByMenteeId(@Param("menteeId") Long menteeId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE mentorship m SET m.mentor_id = m.mentee_id WHERE m.mentor_id = :mentorI",
    nativeQuery = true)
    void deactivateMentor(@Param("mentorId") Long mentorId);

    @Modifying
    @Transactional
    @Query (value = "DELETE mentorship m WHERE m.mentee_id = :menteeId",
    nativeQuery = true)
    void deleteDeactivatedMentee(@Param("menteeId") Long menteeId);

}
