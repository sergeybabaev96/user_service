package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

@Repository
public interface MentorshipRepository extends JpaRepository<User, Long> {
    @Query(nativeQuery = true, value = """
            SELECT EXISTS (
                SELECT 1 FROM mentorship
                WHERE mentor_id = :mentorId
                AND mentee_id = :menteeId
            )
            """)
    boolean existsByMentorIdAndMenteeId(@Param("mentorId") long mentorId,
                                        @Param("menteeId") long menteeId);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM mentorship
            WHERE mentor_id = :mentorId
            AND mentee_id = :menteeId
            """)
    void deleteByMentorIdAndMenteeId(@Param("mentorId") long mentorId,
                                     @Param("menteeId") long menteeId);

    @Query(nativeQuery = true, value = """
            DELETE FROM mentorship
            WHERE mentor_id = :mentorId
            """)
    @Modifying
    void deleteByMentorId( long mentorId);
}
