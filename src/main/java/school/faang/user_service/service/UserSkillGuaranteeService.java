package school.faang.user_service.service;

import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;
import java.util.Optional;

public interface UserSkillGuaranteeService {
    public void createUserSkillGuarantee(long userId, long skillId, long guarantorId);

    public Optional<UserSkillGuarantee> findUserSkillGuaranteeByGuarantorId(long guarantorId);

    public void addUserSkillGuarantee(Long skillId, Long userId);
}
