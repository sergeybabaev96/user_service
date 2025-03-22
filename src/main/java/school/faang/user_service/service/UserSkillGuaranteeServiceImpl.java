package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSkillGuaranteeServiceImpl implements UserSkillGuaranteeService {
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Override
    public void createUserSkillGuarantee(long userId, long skillId, long guarantorId) {
        userSkillGuaranteeRepository.create(userId, skillId, guarantorId);
    }

    @Override
    public Optional<UserSkillGuarantee> findUserSkillGuaranteeByGuarantorId(long guarantorId) {
        return userSkillGuaranteeRepository.findByGuarantorId(guarantorId);
    }
}
