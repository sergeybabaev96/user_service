package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;

    @Spy
    private SkillMapperImpl skillMapper;

    @InjectMocks
    private SkillService skillService;

    @Test
    public void testCreateExistentSkill() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("java");
        when(skillRepository.existsByTitle(skillDto.getTitle()))
                .thenReturn(true);
        assertThrows(DataValidationException.class,
                () -> skillService.create(skillDto));
    }

    @Test
    public void testCreateSkill() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("java");
        when(skillRepository.existsByTitle(skillDto.getTitle()))
                .thenReturn(false);
        skillService.create(skillDto);
        verify(skillRepository, times(1)).save(any());
    }
}
