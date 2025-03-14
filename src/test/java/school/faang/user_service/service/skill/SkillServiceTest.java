package school.faang.user_service.service.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SkillRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Test
    public void testAllSkillsExist() {
        List<Long> skillIds = mockResultSkillExistById();

        assertTrue(getResult(skillIds));
    }

    @Test
    public void testNotAllSkillsExist() {
        long notExistSkillId = 5;
        List<Long> skillIds = mockResultSkillExistById();
        skillIds.add(notExistSkillId);
        when(skillRepository.existsById(notExistSkillId)).thenReturn(false);

        assertFalse(getResult(skillIds));
    }

    @Test
    public void testAssignSkillToUser() {
        long skillId = anyLong();
        long userId = anyLong();

        skillService.assignSkillToUser(skillId, userId);

        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);
    }

    private List<Long> mockResultSkillExistById() {
        List<Long> skillIds = new ArrayList<>(List.of(0L, 1L, 2L, 3L, 4L));
        skillIds.forEach(skillId ->
                when(skillRepository.existsById(skillId)).thenReturn(true));
        return skillIds;
    }

    private boolean getResult(List<Long> skillIds) {
        boolean result = skillService.isAllSkillsExist(skillIds);

        verify(skillRepository, times(skillIds.size())).existsById(anyLong());
        return result;
    }
}