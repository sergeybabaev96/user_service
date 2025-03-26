package school.faang.user_service.controller.goal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private String nullVal;
    private final long userId = 1;
    private final long goalId = 1;
    private final String title = "title";
    private final String description = "description";
    private final String titleName = "title";
    private final String descriptionName = "description";
    private final String createGoalUrl = "/goal?userId=" + userId;
    private final String updateOrDeleteGoalUrl = "/goal/" + goalId;
    private final String getSubtasksByGoalIdUrl = updateOrDeleteGoalUrl + "/subtasks";
    private final String getGoalsByUserIdUrl =
            String.format("/goal?userId=%d&titlePattern=%s&descriptionPattern=%s", userId, title, description);
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

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void testNotCreateWhenInvalidUserId(int userId) throws Exception {
        badRequestWhenInvalidId(() -> post("/goal?userId=" + userId), "createGoal.userId");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"   "})
    public void testNotCreateWhenInvalidTitle(String title) throws Exception {
        badRequestWhenInvalidField(() -> post(createGoalUrl),
                goalDto::setTitle, title, titleName);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"   "})
    public void testNotCreateWhenInvalidDescription(String description) throws Exception {
        badRequestWhenInvalidField(() -> post(createGoalUrl),
                goalDto::setDescription, description, descriptionName);
    }


    @Test
    public void testUpdateGoal() throws Exception {
        when(goalService.updateGoal(goalId, goalDto)).thenReturn(expectedGoalDto);
        expectedOkWhen(() -> put(updateOrDeleteGoalUrl));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void testNotUpdateWhenInvalidGoalId(int goalId) throws Exception {
        badRequestWhenInvalidId(() -> put("/goal/" + goalId), "updateGoal.goalId");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"   "})
    public void testNotUpdateWhenInvalidTitle(String title) throws Exception {
        badRequestWhenInvalidField(() -> put(updateOrDeleteGoalUrl),
                goalDto::setTitle, title, titleName);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"   "})
    public void testNotUpdateWhenInvalidDescription(String description) throws Exception {
        badRequestWhenInvalidField(() -> put(updateOrDeleteGoalUrl),
                goalDto::setDescription, description, descriptionName);
    }

    @Test
    public void testDeleteGoal() throws Exception {
        mockMvc.perform(delete(updateOrDeleteGoalUrl)).andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void testNotDeleteGoalWhenInvalidGoalId(int goalId) throws Exception {
        badRequestWhenInvalidId(() -> delete("/goal/" + goalId), "deleteGoal.goalId");
    }

    @Test
    public void testGetSubtasksByGoalId() throws Exception {
        when(goalService.getSubtasksByGoalId(goalId, filter)).thenReturn(goalDtos);

        mockMvc.perform(get(getSubtasksByGoalIdUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filter)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void testNotGetSubtasksWhenInvalidGoalId(int goalId) throws Exception {
        badRequestWhenInvalidId(() -> get("/goal/" + goalId + "/subtasks"), "getSubtasksByGoalId.goalId");
    }

    @Test
    public void testGetGoalsByUserId() throws Exception {
        when(goalService.getGoalsByUserId(userId, filter)).thenReturn(goalDtos);

        mockMvc.perform(get(getGoalsByUserIdUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filter)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void testNotGetGoalsWhenInvalidUserId(int userId) throws Exception {
        badRequestWhenInvalidId(() -> get("/goal?userId=" + userId), "getGoalsByUserId.userId");
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

    private void badRequestWhenInvalidId(Supplier<MockHttpServletRequestBuilder> builder, String argumentName) throws Exception {
        mockMvc.perform(builder.get()
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(goalDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(argumentName + ": can`t be less than 1"))
                .andReturn();
    }

    private void badRequestWhenInvalidField(Supplier<MockHttpServletRequestBuilder> builder,
                                            Consumer<String> fieldSetter, String val, String field) throws Exception {
        fieldSetter.accept(val);

        mockMvc.perform(builder.get()
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(goalDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("{\"%s\":\"can't be blank or null \"}", field)))
                .andReturn();
    }
}
