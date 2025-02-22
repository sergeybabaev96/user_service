package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

import java.util.List;

@Repository
public interface MentorshipRepository extends JpaRepository<User, Long> {

    List<User> findMenteesById(Long userId);

    List<User> findMentorsById(Long userId);

    @Query(nativeQuery = true, value = """
            UPDATE mentorship SET mentor_id = mentee_id
            WHERE mentor_id = :userId
            """)
    @Modifying
    void deactivateMentorship(long userId);
}
