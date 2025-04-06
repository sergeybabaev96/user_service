package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferServiceImpl skillOfferService;

    @Mock
    private UserSkillGuaranteeServiceImpl skillUserGuarantee;

    @Spy
    private SkillMapperImpl skillMapper;

    @InjectMocks
    private SkillServiceImpl skillService;


    @Test
    public void testCreateWithExistingTitle() {
        SkillDto skillDto = new SkillDto(null,"Java");
        when(skillRepository.existsByTitle(skillDto.title())).thenReturn(true);
        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testCreate() {
        SkillDto skillDto = new SkillDto(null,"Java");
        when(skillRepository.existsByTitle(skillDto.title())).thenReturn(false);
        SkillDto result = skillService.create(skillDto);
        verify(skillMapper, times(1)).toEntity(skillDto);
        verify(skillRepository, times(1)).save(any(Skill.class));
        assertEquals("Java", result.title());
    }

    @Test
    public void testGetUserSkills() {
        long userId = 1L;
        Skill skill1 = Skill.builder().id(1).title("Java").build();
        Skill skill2 = Skill.builder().id(2).title("Python").build();
        Skill skill3 = Skill.builder().id(3).title("JavaScript").build();
        List<Skill> skills = List.of(skill1, skill2, skill3);
        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);
        List<SkillDto> result = skillService.getUserSkills(1L);
        verify(skillRepository, times(1)).findAllByUserId(userId);
        assertEquals(3, result.size());

    }

    @Test
    public void testGetOfferedSkills() {
        long userId = 1L;
        Skill skill1 = Skill.builder().id(1L).title("Java").build();
        Skill skill2 = Skill.builder().id(2L).title("Python").build();
        Skill skill3 = Skill.builder().id(1L).title("Java").build();

        List<Skill> offeredSkills = List.of(skill1, skill2, skill3);

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(offeredSkills);

        List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);

        assertEquals(2, result.size());

        Map<Long, Long> expectedCounts = Map.of(1L, 2L, 2L, 1L);

        for (SkillCandidateDto dto : result) {
            assertTrue(expectedCounts.containsKey(dto.skill().id()));
            assertEquals(expectedCounts.get(dto.skill().id()), dto.offersAmount());
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
        assertEquals("Java", skillDto.title());
    }
    @Test
    public void testDoesSkillExists_RequestDataFromRepository_ReturnsTrue() {
        var skillId = 10L;
        when(skillRepository.existsById(skillId)).thenReturn(true);

        var result = skillService.doesSkillExists(skillId);

        verify(skillRepository, times(1)).existsById(skillId);
        assertTrue(result);
    }
}
