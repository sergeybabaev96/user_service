package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserSkillGuaranteeServiceTest {

    @InjectMocks
    private UserSkillGuaranteeService userSkillGuaranteeService;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Test
    void testSave(){
        UserSkillGuarantee skillGuarantee = new UserSkillGuarantee();

        userSkillGuaranteeService.save(skillGuarantee);
        verify(userSkillGuaranteeRepository, times(1)).save(skillGuarantee);
    }
}