package school.faang.user_service.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.skill.SkillService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    @Test
    public void testCreateWithNullableTitle(){
        SkillDto skillDto = new SkillDto();
        assertThrows(DataValidationException.class, () -> skillController.create(skillDto));
    }

    @Test
    public void testCreateWithEmptyTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("");
        assertThrows(DataValidationException.class, () -> skillController.create(skillDto));
    }

    @Test
    public void testCreateWithBlankTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("   ");
        assertThrows(DataValidationException.class, () -> skillController.create(skillDto));
    }

    @Test
    public void testCreate() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("title");
        skillController.create(skillDto);
        verify(skillService, times(1)).create(skillDto);
    }
}
