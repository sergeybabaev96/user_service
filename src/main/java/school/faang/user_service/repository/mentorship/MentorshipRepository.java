package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.Optional;

public interface MentorshipRepository extends CrudRepository<User, Long> {

    @Query(nativeQuery = true, value = """
            SELECT m.* FROM mentorship m
            JOIN users u ON u.id = m.mentor_id
            WHERE m.mentor_id = :userId
            """)
    List<User> findAllMenteesByUserId(long userId);

    @Query(nativeQuery = true, value = """
            SELECT m.* FROM mentorship m
            JOIN users u ON u.id = m.mentee_id
            WHERE u.mentee_id = :userId
            """)
    List<User> findAllMentorsByUserId(long userId);

    Optional<User> findUserById(long id);
}
