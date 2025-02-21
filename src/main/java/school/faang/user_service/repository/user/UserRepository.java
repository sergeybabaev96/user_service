package school.faang.user_service.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = """
            SELECT COUNT(s.id) FROM users u
            JOIN user_skill us ON us.user_id = u.id
            JOIN skill s ON us.skill_id = s.id
            WHERE u.id = ?1 AND s.id IN (?2)
            """)
    int countOwnedSkills(long userId, List<Long> ids);

    @Query(nativeQuery = true, value = """
            SELECT u.* FROM users u
            JOIN user_premium up ON up.user_id = u.id
            WHERE up.end_date > NOW()
            """)
    Page<User> findPremiumUsers(Pageable pageable);

    List<User> findByUsernameLike(String username);

    @Query(nativeQuery = true, value = """
            SELECT u.* FROM users u
                        JOIN user_goal ug ON u.id = ug.user_id
                        WHERE ug.goal_id = :goalId
            """)
    List<User> findUsersByGoalId(long goalId);
}