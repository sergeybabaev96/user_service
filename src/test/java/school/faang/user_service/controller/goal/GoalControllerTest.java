package school.faang.user_service.controller.goal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import school.faang.user_service.config.context.UserContext;
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
import java.util.function.Consumer;
import java.util.function.Supplier;

@WebMvcTest(GoalController.class)
public class GoalControllerTest {

    @MockBean
    private GoalService goalService;

    @MockBean
    private UserContext userContext;

    @Autowired
    private MockMvc mockMvc;

    private final long userId = 1;
    private final long goalId = 1;
    private final String createGoalUrl = "/goal?userId=" + userId;
    private final String updateOrDeleteGoalUrl = "/goal/" + goalId;
    private final String getSubtasksByGoalIdUrl = updateOrDeleteGoalUrl + "/subtasks";
    private final String getGoalsByUserIdUrl = "/goal/filter?userId=" + userId;
    private final String title = "title";
    private final String description = "description";
    private final Goal parent = Goal.builder().id(1L).build();

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
        expectedOkWhen(() -> post(createGoalUrl));
    }

    @Test
    public void testNotCreateWithNullTitle() throws Exception {
        badRequestWhenInvalidField(() -> post(createGoalUrl),
                goalDto::setTitle, null, "Invalid title: ");
    }


    @Test
    public void testNotCreateWithBlankTitle() throws Exception {
        badRequestWhenInvalidField(() -> post(createGoalUrl),
                goalDto::setTitle, "   ", "Invalid title: ");
    }

    @Test
    public void testNotCreateWithNullDescription() throws Exception {
        badRequestWhenInvalidField(() -> post(createGoalUrl),
                goalDto::setDescription, null, "Invalid description: ");
    }


    @Test
    public void testNotCreateWithBlankDescription() throws Exception {
        badRequestWhenInvalidField(() -> post(createGoalUrl),
                goalDto::setDescription, "   ", "Invalid description: ");
    }

    @Test
    public void testUpdateGoal() throws Exception {
        when(goalService.updateGoal(goalId, goalDto)).thenReturn(expectedGoalDto);
        expectedOkWhen(() -> put(updateOrDeleteGoalUrl));
    }

    @Test
    public void testNotUpdateWithNullTitle() throws Exception {
        badRequestWhenInvalidField(() -> put(updateOrDeleteGoalUrl),
                goalDto::setTitle, null, "Invalid title: ");
    }

    @Test
    public void testDeleteGoal() throws Exception {
        mockMvc.perform(delete(updateOrDeleteGoalUrl)).andExpect(status().isOk());
    }

    @Test
    public void testGetSubtasksByGoalId() throws Exception {
        when(goalService.getSubtasksByGoalId(goalId, filter)).thenReturn(goalDtos);

        mockMvc.perform(post(getSubtasksByGoalIdUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filter)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetSubtasksByUserId() throws Exception {
        when(goalService.getGoalsByUserId(userId, filter)).thenReturn(goalDtos);

        mockMvc.perform(post(getGoalsByUserIdUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filter)))
                .andExpect(status().isOk());
    }

    private void expectedOkWhen(Supplier<MockHttpServletRequestBuilder> builder) throws Exception {
        mockMvc.perform(builder.get()
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

    private void badRequestWhenInvalidField(Supplier<MockHttpServletRequestBuilder> builder, Consumer<String> fieldSetter,
                                            String field, String message) throws Exception {
        fieldSetter.accept(field);

        mockMvc.perform(builder.get()
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(goalDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(message + field))
                .andReturn();
    }
}
