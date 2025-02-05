package school.faang.user_service.repository.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.UserSkillGuarantee;

public interface UserSkillGuaranteeRepository extends CrudRepository<UserSkillGuarantee, Long> {
    @Query(nativeQuery = true, value = "INSERT INTO user_skill_guarantee (user_id, skill_id,guarantor_id) VALUES (?1, ?2, ?3) returning id")
    Long create(Long userId, Long skillId, Long guarantorId);

}