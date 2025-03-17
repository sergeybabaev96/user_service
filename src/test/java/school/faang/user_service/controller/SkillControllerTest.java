package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.persistence.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.dto.skill.SkillDto;
import school.faang.user_service.service.implementation.SkillServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SkillController.class)
@Import({SkillControllerTest.TestExceptionHandler.class})
class SkillControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private SkillServiceImpl skillService;
    @MockBean
    private UserContext userContext;

    @Test
    void testCreate() throws Exception {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(1L);
        skillDto.setTitle("Title1");
        ObjectMapper om = new ObjectMapper();
        ObjectWriter ow = om.writer();
        String request = ow.writeValueAsString(skillDto);
        when(skillService.create(skillDto)).thenReturn(skillDto);

        mockMvc.perform(post("/skill/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(dto -> assertNotNull(skillDto))
                .andExpect(id -> assertEquals(skillDto.getId(), 1L))
                .andExpect(title -> assertEquals(skillDto.getTitle(), "Title1"))
                .andReturn();

        verify(skillService, times(1)).create(skillDto);
    }

    @Test
    void testGetUserSkills() throws Exception {
        User user = new User();
        Long userId = 1L;
        user.setId(userId);
        SkillDto firstSkillDto = new SkillDto();
        firstSkillDto.setId(1L);
        firstSkillDto.setTitle("Title1");
        SkillDto secondSkillDto = new SkillDto();
        secondSkillDto.setId(2L);
        secondSkillDto.setTitle("Title2");
        List<SkillDto> userSkills = List.of(firstSkillDto, secondSkillDto);
        when(skillService.getUserSkills(user.getId())).thenReturn(userSkills);

        mockMvc.perform(get("/skill/user-skills/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(id -> assertNotNull(userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Title1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("Title2"));

        verify(skillService, times(1)).getUserSkills(user.getId());
    }

    @Test
    void testGetOfferedSkills() throws Exception {
        User user = new User();
        Long userId = 1L;
        user.setId(userId);
        List<SkillCandidateDto> skillCandidates = getSkillCandidatesDto();
        when(skillService.getOfferedSkills(user.getId())).thenReturn(skillCandidates);

        mockMvc.perform(get("/skill/skills-offered/{userId}", userId))
                .andExpect(id -> assertNotNull(userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].skill").value(skillCandidates.get(0).getSkill()))
                .andExpect(jsonPath("$[0].offersAmount").value(skillCandidates.get(0).getOffersAmount()))
                .andExpect(jsonPath("$[1].skill").value(skillCandidates.get(1).getSkill()))
                .andExpect(jsonPath("$[1].offersAmount").value(skillCandidates.get(1).getOffersAmount()));

        verify(skillService, times(1)).getOfferedSkills(user.getId());
    }

    @Test
    void testAcquireSkillFromOffers() throws Exception {
        User user = new User();
        Long userId = 1L;
        user.setId(userId);
        Skill skill = new Skill();
        Long skillId = 1L;
        skill.setId(skillId);
        SkillDto skillDto = new SkillDto();
        skillDto.setId(1L);
        skillDto.setTitle("Title");
        when(skillService.acquireSkillFromOffers(skill.getId(), user.getId())).thenReturn(skillDto);

        mockMvc.perform(get("/skill/acquire/{skillId}/user/{userId}", skillId, userId))
                .andExpect(id -> assertNotNull(skillId))
                .andExpect(id -> assertNotNull(userId))
                .andExpect(status().isOk())
                .andExpect(dto -> assertNotNull(skillDto))
                .andExpect(id -> assertEquals(skillDto.getId(), 1L))
                .andExpect(title -> assertEquals(skillDto.getTitle(), "Title"))
                .andReturn();

        verify(skillService, times(1)).acquireSkillFromOffers(skill.getId(), user.getId());
    }

    private static @NotNull List<SkillCandidateDto> getSkillCandidatesDto() {
        SkillDto firstSkillDto = new SkillDto();
        firstSkillDto.setId(1L);
        SkillDto secondSkillDto = new SkillDto();
        secondSkillDto.setId(2L);
        SkillCandidateDto firstSkillCandidateDto = new SkillCandidateDto();
        firstSkillCandidateDto.setSkill(firstSkillDto);
        firstSkillCandidateDto.setOffersAmount(1L);
        SkillCandidateDto secondSkillCandidateDto = new SkillCandidateDto();
        secondSkillCandidateDto.setSkill(firstSkillDto);
        secondSkillCandidateDto.setOffersAmount(1L);
        return List.of(firstSkillCandidateDto, secondSkillCandidateDto);
    }

    @ControllerAdvice
    static class TestExceptionHandler {
        @ExceptionHandler(EntityNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public void handleEntityNotFoundException(EntityNotFoundException ex) {
        }
    }
}
