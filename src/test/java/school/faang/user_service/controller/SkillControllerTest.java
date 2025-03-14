package school.faang.user_service.controller;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.dto.skill.SkillDto;
import school.faang.user_service.service.implementation.SkillServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {
    @InjectMocks
    private SkillController skillController;
    @Mock
    private SkillServiceImpl skillService;

    @Test
    void testCreate() {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(1L);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ArgumentCaptor<SkillDto> skillDtoCaptor = ArgumentCaptor.forClass(SkillDto.class);
        when(skillService.create(skillDto)).thenReturn(skillDto);

        SkillDto actualSkillDto = skillController.create(skillDto);

        verify(skillService, times(1)).create(skillDtoCaptor.capture());
        SkillDto skillDtoValue = skillDtoCaptor.getValue();
        assertEquals(skillDto, skillDtoValue);
        assertEquals(skillDto.getId(), actualSkillDto.getId());
    }

    @Test
    void testGetUserSkills() {
        User user = new User();
        user.setId(1L);
        SkillDto firstSkillDto = new SkillDto();
        firstSkillDto.setId(1L);
        SkillDto secondSkillDto = new SkillDto();
        secondSkillDto.setId(2L);
        List<SkillDto> userSkills = List.of(firstSkillDto, secondSkillDto);
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        when(skillService.getUserSkills(user.getId())).thenReturn(userSkills);

        List<SkillDto> actualUserSkills = skillController.getUserSkills(user.getId());

        verify(skillService, times(1)).getUserSkills(userIdCaptor.capture());
        Long userIdValue = userIdCaptor.getValue();
        assertEquals(user.getId(), userIdValue);
        assertEquals(userSkills.size(), actualUserSkills.size());
        assertEquals(userSkills.get(0).getId(), actualUserSkills.get(0).getId());
        assertEquals(userSkills.get(1).getId(), actualUserSkills.get(1).getId());
    }

    @Test
    void testGetOfferedSkills() {
        User user = new User();
        user.setId(1L);
        List<SkillCandidateDto> skillCandidates = getSkillCandidatesDto();
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        when(skillService.getOfferedSkills(user.getId())).thenReturn(skillCandidates);

        List<SkillCandidateDto> actualOfferedSkills = skillController.getOfferedSkills(user.getId());

        verify(skillService, times(1)).getOfferedSkills(userIdCaptor.capture());
        Long userIdValue = userIdCaptor.getValue();
        assertEquals(user.getId(), userIdValue);
        assertEquals(skillCandidates.size(), actualOfferedSkills.size());
        assertEquals(skillCandidates.get(0).getSkill(), actualOfferedSkills.get(0).getSkill());
        assertEquals(skillCandidates.get(1).getSkill(), actualOfferedSkills.get(1).getSkill());
    }

    @Test
    void testAcquireSkillFromOffers() {
        User user = new User();
        user.setId(1L);
        Skill skill = new Skill();
        skill.setId(1L);
        SkillDto skillDto = new SkillDto();
        skillDto.setId(1L);
        ArgumentCaptor<Long> skillIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        when(skillService.acquireSkillFromOffers(skill.getId(), user.getId())).thenReturn(skillDto);

        SkillDto actualSkillDto = skillController.acquireSkillFromOffers(skill.getId(), user.getId());

        verify(skillService, times(1))
                .acquireSkillFromOffers(skillIdCaptor.capture(), userIdCaptor.capture());
        Long skillIdValue = skillIdCaptor.getValue();
        Long userIdValue = userIdCaptor.getValue();
        assertEquals(skill.getId(), skillIdValue);
        assertEquals(user.getId(), userIdValue);
        assertEquals(skillDto.getId(), actualSkillDto.getId());
    }

    private static @NotNull List<SkillCandidateDto> getSkillCandidatesDto() {
        SkillDto firstSkillDto = new SkillDto();
        firstSkillDto.setId(1L);
        SkillDto secondSkillDto = new SkillDto();
        secondSkillDto.setId(2L);
        SkillCandidateDto firstSkillCandidateDto = new SkillCandidateDto();
        firstSkillCandidateDto.setSkill(firstSkillDto);
        SkillCandidateDto secondSkillCandidateDto = new SkillCandidateDto();
        secondSkillCandidateDto.setSkill(firstSkillDto);
        return List.of(firstSkillCandidateDto, secondSkillCandidateDto);
    }
}