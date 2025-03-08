package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.UserSkillGuarantee;

import java.util.List;

public interface UserSkillGuaranteeRepository extends JpaRepository<UserSkillGuarantee, Long> {

    List<UserSkillGuarantee> findBySkillIdInAndGuarantorIdAndUserId(List<Long> skillId, Long guarantorId, Long userId);
}