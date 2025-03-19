package school.faang.user_service.controller.goal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.GoalService;


import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class GoalControllerTest {

    @MockBean
    private GoalService goalService;

    @Autowired
    private MockMvc mockMvc;

    private final String title = "title";
    private final String description = "description";
    private final Goal parent = Goal.builder().id(1L).build();
    private final long userId = 1;
    private final long goalId = 1;

    private final GoalFilterDto filter = GoalFilterDto.builder().build();
    private GoalDto goalDto;
    private final List<Skill> skills = List.of(
            Skill.builder().id(0).build(),
            Skill.builder().id(1).build(),
            Skill.builder().id(2).build());

    private final GoalDto expectedGoalDto = GoalDto.builder()
            .id(2L)
            .title(title)
            .description(description)
            .skillIds(skills.stream().map(Skill::getId).toList())
            .parentId(parent.getId())
            .status(GoalStatus.ACTIVE)
            .build();

    private final List<GoalDto> goalDtos = List.of(
            GoalDto.builder().build(),
            GoalDto.builder().build(),
            GoalDto.builder().build());

    @BeforeEach
    public void setUp() {
        goalDto = GoalDto.builder()
                .title(title)
                .description(description)
                .skillIds(skills.stream().map(Skill::getId).toList())
                .parentId(parent.getId())
                .build();
    }

    @Test
    public void testCreateGoal() throws Exception {
        when(goalService.createGoal(userId, goalDto)).thenReturn(expectedGoalDto);

        mockMvc.perform(post("/goal/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(goalDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.title", is("title")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.skillIds", hasSize(3)))
                .andExpect(jsonPath("$.skillIds[0]", is(0)))
                .andExpect(jsonPath("$.skillIds[1]", is(1)))
                .andExpect(jsonPath("$.skillIds[2]", is(2)))
                .andExpect(jsonPath("$.parentId", is(1)))
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    public void testNotCreateWithNullTitle() throws Exception {
        goalDto.setTitle(null);

        mockMvc.perform(post("/goal/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(goalDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid title: null"))
                .andReturn();
    }

    @Test
    public void testNotCreateWithBlankTitle() throws Exception {
        goalDto.setTitle("   ");

        mockMvc.perform(post("/goal/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(goalDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid title:    "))
                .andReturn();
    }

    @Test
    public void testNotCreateWithNullDescription() throws Exception {
        goalDto.setDescription(null);

        mockMvc.perform(post("/goal/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(goalDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid description: null"))
                .andReturn();
    }

    @Test
    public void testNotCreateWithBlankDescription() throws Exception {
        goalDto.setDescription("   ");

        mockMvc.perform(post("/goal/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(goalDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid description:    "))
                .andReturn();
    }

    @Test
    public void testUpdateGoal() throws Exception {
        when(goalService.updateGoal(goalId, goalDto)).thenReturn(expectedGoalDto);

        mockMvc.perform(put("/goal/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(goalDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.title", is("title")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.skillIds", hasSize(3)))
                .andExpect(jsonPath("$.skillIds[0]", is(0)))
                .andExpect(jsonPath("$.skillIds[1]", is(1)))
                .andExpect(jsonPath("$.skillIds[2]", is(2)))
                .andExpect(jsonPath("$.parentId", is(1)))
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    public void testNotUpdateWithNullTitle() throws Exception {
        goalDto.setTitle(null);

        mockMvc.perform(put("/goal/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(goalDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid title: null"))
                .andReturn();


    }

    @Test
    public void testDeleteGoal() throws Exception {
        mockMvc.perform(delete("/goal/1")).andExpect(status().isOk());
    }

    @Test
    public void testGetSubtasksByGoalId() throws Exception {
        when(goalService.getSubtasksByGoalId(goalId, filter)).thenReturn(goalDtos);

        mockMvc.perform(post("/goal/1/subtasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filter)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetSubtasksByUserId() throws Exception {
        when(goalService.getGoalsByUserId(userId, filter)).thenReturn(goalDtos);

        mockMvc.perform(post("/1/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filter)))
                .andExpect(status().isOk());
    }

}
