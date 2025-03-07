package school.faang.user_service.repository.mentorship;

import feign.Param;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.User;

import java.util.List;

public interface MentorshipRepository extends CrudRepository<User, Long> {
    @Query("SELECT u FROM User u JOIN u.mentors m WHERE m.id = :mentorId")
    List<User> findAllMenteesByMentorId(@Param("mentorId") Long mentorId);
}

