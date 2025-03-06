package school.faang.user_service.service.skill;


import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.ResponseSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class SkillServiceImplTest {
    @Mock
    private SkillRepository skillRepository;

    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @InjectMocks
    private SkillServiceImpl skillService;

    @Test
    public void testCreateSuccess() {
        CreateSkillDto skillDto = new CreateSkillDto(1L, "John");
        Skill skill = skillMapper.toSkillEntity(skillDto);

        Mockito.when(skillRepository.save(skill)).thenReturn(skill);
        ResponseSkillDto result = skillService.create(skillDto);

        assertEquals(skillDto.title(), result.title());
    }

    @Test
    @DisplayName("Testing return DataValidationException")
    public void testCreateTitleIsExistsFailed() {
        CreateSkillDto skillDto = new CreateSkillDto(1L, "John");

        Mockito.when(skillRepository.existsByTitle(skillDto.title()))
                .thenThrow(new DataValidationException("DataValidationException!!!"));
        Assert.assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    @DisplayName("get skills from Repo")
    public void testGetUserSkillsSuccess() {
        List<ResponseSkillDto> responseSkillDtos = new ArrayList<>();
        List<ResponseSkillDto> result = skillService.getUserSkills(1L);

        assertEquals(responseSkillDtos, result);
    }

    @Test
    @DisplayName("Get offered skills for user")
    public void testGetOfferedSkillsSuccess() {
        List<SkillCandidateDto> skillCandidateDtos = new ArrayList<>();
        List<SkillCandidateDto> result = skillService.getOfferedSkills(1L);

        assertEquals(skillCandidateDtos, result);
    }

    @Test
    public void testAcquireSkillFromOffersSkillNotFoundFailed() {
        Mockito.when(skillRepository.findById(1L))
                .thenThrow(new DataValidationException("DataValidationException!!!"));
        Assert.assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(1L, 1L));
    }

}
