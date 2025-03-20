package school.faang.user_service.service.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferService skillOfferService;

    @Mock
    private SkillUserGuarantee skillUserGuarantee;

    @Spy
    private SkillMapperImpl skillMapper;

    @InjectMocks
    private SkillService skillService;

    @Test
    public void testCreateWithExistingTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("Java");
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);
        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testCreate() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("Java");
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
        skillService.create(skillDto);
        verify(skillMapper, times(1)).toEntity(skillDto);
        verify(skillRepository, times(1)).save(any(Skill.class));
    }

    @Test
    public void testGetUserSkills() {
        when(skillRepository.findAllByUserId(1L)).thenReturn(new ArrayList<Skill>());
        skillService.getUserSkills(1L);
    }

    @Test
    public void testGetOfferedSkills() {
        Long userId = 1L;
        Skill skill1 = new Skill();
        skill1.setTitle("Java");
        skill1.setId(1L);
        Skill skill2 = new Skill();
        skill2.setTitle("Python");
        skill2.setId(2L);
        Skill skill3 = new Skill();
        skill3.setId(1L);
        skill3.setTitle("Java");

        List<Skill> offeredSkills = List.of(skill1, skill2, skill3);

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(offeredSkills);

        List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);

        assertEquals(2, result.size());

        Map<Long, Long> expectedCounts = Map.of(1L, 2L, 2L, 1L);

        for (SkillCandidateDto dto : result) {
            assertTrue(expectedCounts.containsKey(dto.getSkill().getId()));
            assertEquals(expectedCounts.get(dto.getSkill().getId()), dto.getOffersAmount());
        }

        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);

        verify(skillMapper, times(2)).toDto(any(Skill.class));
    }

    @Test
    public void testAcquireSkillFromOffersWithExistingSkill() {
        long userId = 1L;
        long skillId = 2L;
        Skill existingSkill = new Skill();
        existingSkill.setTitle("Java");
        existingSkill.setId(1L);

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(existingSkill));
        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    public void testAcquireSkillFromOffersWithNonExistingSkill() {

        long userId = 1L;
        long skillId = 2L;
        Skill newSkill = new Skill();
        newSkill.setTitle("Java");
        newSkill.setId(1L);
        when(skillRepository.findUserSkill(skillId, userId))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(newSkill));
        SkillDto skillDto = skillService.acquireSkillFromOffers(skillId, userId);
        verify(skillOfferService, times(1)).isEnoughAmountOffersToSkill(skillId, userId);
        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);
        verify(skillUserGuarantee, times(1)).addUserSkillGuarantee(skillId, userId);
        verify(skillMapper, times(1)).toDto(newSkill);
        assertEquals("Java", skillDto.getTitle());
    }

}
