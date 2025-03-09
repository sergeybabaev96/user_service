package school.faang.user_service.repository.mentorship;

import feign.Param;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

import java.util.List;

@Repository
public interface MentorshipRepository extends CrudRepository<User, Long> {
    @Modifying
    @Query(nativeQuery = true, value = """
            SELECT u.* FROM users u INNER JOIN mentorship m ON u.id = m.mentor_id WHERE m.id = :mentorId
            """)
    List<User> findAllMenteesByMentorId(@Param("mentorId") Long mentorId);
}

