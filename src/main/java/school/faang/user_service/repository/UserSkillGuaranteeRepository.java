package school.faang.user_service.repository;

import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.UserSkillGuarantee;

import java.util.Optional;

public interface UserSkillGuaranteeRepository extends CrudRepository<UserSkillGuarantee, Long> {

    Optional<UserSkillGuarantee> findBySkillIdAndGuarantorIdAndUserId(Long skillId, Long guarantorId, Long userId);
}