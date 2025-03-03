package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;

public interface UserSkillGuaranteeRepository extends CrudRepository<UserSkillGuarantee, Long> {
    @Query(nativeQuery = true, value = """
                    INSERT INTO user_skill_guarantee  (id, user, skill, guarantor)
                    VALUES (id, user, skill, guarantor)
                    """)
    void addGuarantor(Long id, User user, Skill skill, User guarantor);
}