package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;

import java.util.List;

public interface UserSkillGuaranteeRepository extends CrudRepository<UserSkillGuarantee, Long> {

    @Query(nativeQuery = true, value = """
            SELECT s.* FROM skill s
            JOIN user_skill_guarantee usg ON s.id = usg.skill_id
            WHERE usg.user_id = ?1 AND usg.guarantor_id = ?2
            """)
    List<Skill> findAllSkillsGuaranteedToUserByGuarantee(long userId, long guarantorId);
}