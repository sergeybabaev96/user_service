package school.faang.user_service.repository.mentorship;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

@Repository
public interface MentorshipRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM mentorship
            WHERE mentor_id = :mentorId
            AND mentee_id = :menteeId
            """)
    void deleteMentorship(@Param("mentorId") long mentorId, @Param("menteeId") long menteeId);

    @Query(nativeQuery = true, value = """
            SELECT COUNT(*) > 0 FROM mentorship
            WHERE mentor_id = :mentorId
            AND menteeId = :menteeId
            """)
    boolean existsByMentorIdAndMenteeId(@Param("mentorId") long mentorId, @Param("menteeId") long menteeId);
}
