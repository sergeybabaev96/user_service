package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;

@Service
@RequiredArgsConstructor
public class UserSkillGuaranteeService {
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserService userService;

    public void saveUserSkillGuarantee(Long userId, Skill skill, Long guarantorId) {
        UserSkillGuarantee userSkillGuarantee = buildUserSkillGuarantee(userId, skill, guarantorId);
        userSkillGuaranteeRepository.save(userSkillGuarantee);
    }

    private UserSkillGuarantee buildUserSkillGuarantee(Long userId, Skill skill, Long guarantorId) {
        UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
        User user = userService.getUser(userId);
        User guarantor = userService.getUser(guarantorId);
        userSkillGuarantee.setUser(user);
        userSkillGuarantee.setSkill(skill);
        userSkillGuarantee.setGuarantor(guarantor);
        return userSkillGuarantee;
    }
}
