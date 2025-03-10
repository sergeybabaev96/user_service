package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.UserSkillGuarantee;

import java.util.Optional;

public interface UserSkillGuaranteeRepository extends CrudRepository<UserSkillGuarantee, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO user_skill_guarantee (user_id, skill_id, guarantor_id)
            VALUES (?1, ?2, ?3)
            """)
    @Modifying
    @Transactional
    void create(long userId, long skillId, long guarantorId);

    Optional<UserSkillGuarantee> findByGuarantorId(Long guarantorId);
}