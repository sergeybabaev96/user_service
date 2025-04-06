package school.faang.user_service.service;

import school.faang.user_service.entity.UserSkillGuarantee;

import java.util.Optional;

public interface UserSkillGuaranteeService {
    void createUserSkillGuarantee(long userId, long skillId, long guarantorId);

    Optional<UserSkillGuarantee> findUserSkillGuaranteeByGuarantorId(long guarantorId);

    void addUserSkillGuarantee(Long skillId, Long userId);
}
