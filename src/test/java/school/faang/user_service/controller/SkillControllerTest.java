package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

//    @Test
//    public void testCreateWithNullableTitle() {
//        SkillDto skill = new SkillDto();
//        assertThrows(DataValidationException.class, () ->
//                skillController.create(skill));
//    }
//
//    @Test
//    public void testCreateWithEmptyTitle() {
//        SkillDto skill = new SkillDto();
//        skill.setTitle("");
//        assertThrows(DataValidationException.class, () ->
//            skillController.create(skill));
//    }
//
//    @Test
//    public void testCreateBlankTitle() {
//        SkillDto skill = new SkillDto();
//        skill.setTitle("    ");
//        assertThrows(DataValidationException.class, () ->
//                skillController.create(skill));
//    }

    @Test
    public void testCreate() {
        SkillDto skill = new SkillDto();
        skill.setTitle("title");
        skillController.create(skill);
        verify(skillService, times(1)).create(skill);
    }
}
