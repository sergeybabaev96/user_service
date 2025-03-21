package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.User;

public interface MentorshipRepository extends CrudRepository<User, Long> {
    @Query(nativeQuery = true, value = """
            DELETE FROM mentorship
            WHERE mentor_id = :mentorId
            """)
    @Modifying
    void deleteByMentorId( long mentorId);
}
