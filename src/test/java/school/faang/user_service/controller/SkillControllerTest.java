package school.faang.user_service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.ResponseSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {
    @Mock
    private SkillServiceImpl skillService;

    @InjectMocks
    private SkillController skillController;

    @Test
    @DisplayName("Check title is valid")
    public void testTitleIsValid() {
        CreateSkillDto createSkill = new CreateSkillDto(1L, "Java");
        ResponseSkillDto responseSkill = new ResponseSkillDto(1L, "Java");

        Mockito.when(skillService.create(any(CreateSkillDto.class))).thenReturn(responseSkill);
        ResponseSkillDto result = skillController.create(createSkill);

        Assertions.assertEquals(createSkill.title(), result.title());
    }

    @Test
    @DisplayName("get skills by id - find")
    public void testListSkillsById() {
        ResponseSkillDto skill1 = new ResponseSkillDto(1L, "Java");
        ResponseSkillDto skill2 = new ResponseSkillDto(2L, "Spring");


        List<ResponseSkillDto> skills = new ArrayList<>();
        skills.add(skill1);
        skills.add(skill2);

        Mockito.when(skillService.getUserSkills(1L)).thenReturn(List.of(skill1, skill2));
        List<ResponseSkillDto> skillDtos = skillController.getUserSkills(1L);

        assertEquals(skills, skillDtos);
    }

    @Test
    @DisplayName("get skills by id - not find")
    public void testEmptyListSkillsById() {
        List<CreateSkillDto> skills = new ArrayList<>();
        Mockito.when(skillService.getUserSkills(1L)).thenReturn(new ArrayList<>());
        List<ResponseSkillDto> skillDtos = skillController.getUserSkills(1L);

        assertEquals(skills, skillDtos);
    }

    @Test
    @DisplayName("Get Offered Skills - success")
    public void testGetOfferedSkillsByIdSuccess() {
        List<SkillCandidateDto> skillCandidateDtos = new ArrayList<>();

        Mockito.when(skillService.getOfferedSkills(1L)).thenReturn(new ArrayList<>());
        List<SkillCandidateDto> skillCandidateDtoReturns = skillController.getOfferedSkills(1L);

        assertEquals(skillCandidateDtos, skillCandidateDtoReturns);
    }

    @Test
    @DisplayName("Acquire Skill From Offers - success")
    public void testAcquireSkillFromOffersSuccess() {
        ResponseSkillDto responseSkillDto = new ResponseSkillDto(1L, "Spring");

        Mockito.when(skillService.acquireSkillFromOffers(1L, 2L))
                .thenReturn(new ResponseSkillDto(1L, "Spring"));
        ResponseSkillDto skillDto = skillController.acquireSkillFromOffers(1L, 2L);

        assertEquals(responseSkillDto, skillDto);
    }

}
