package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.controller.goal.GoalController;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.dto.request.GoalRequest;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.GoalService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {
    private MockMvc mockMvc;

    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController goalController;

    private ObjectMapper objectMapper;

    private final long userId = 1L;
    private final long goalId = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(goalController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateGoalSuccess() throws Exception {
        GoalDto goalDTO = new GoalDto();
        goalDTO.setTitle("New Goal");
        GoalRequest request = new GoalRequest(userId, goalDTO);

        when(goalService.createGoal(userId, goalDTO)).thenReturn(goalDTO);

        mockMvc.perform(post("/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value("New Goal"));

        verify(goalService, times(1)).createGoal(userId, goalDTO);
    }

    @Test
    void testUpdateGoalSuccess() throws Exception {
        GoalDto goalDTO = new GoalDto();
        goalDTO.setId(goalId);
        goalDTO.setTitle("Updated Goal Title");
        GoalRequest request = new GoalRequest(userId, goalDTO);

        when(goalService.updateGoal(userId, goalDTO)).thenReturn(goalDTO);

        mockMvc.perform(put("/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value("Updated Goal Title"));

        verify(goalService, times(1)).updateGoal(userId, goalDTO);
    }

    @Test
    void testDeleteGoalSuccess() throws Exception {
        doNothing().when(goalService).deleteGoal(goalId);

        mockMvc.perform(delete("/goals/{goalId}", goalId))
                .andExpect(status().isNoContent());

        verify(goalService, times(1)).deleteGoal(goalId);
    }

    @Test
    void testGetGoalsByUserSuccess() throws Exception {
        GoalFilterDto filters = new GoalFilterDto();
        GoalDto goal1 = new GoalDto();
        goal1.setTitle("Goal 1");
        GoalDto goal2 = new GoalDto();
        goal2.setTitle("Goal 2");

        when(goalService.getGoalsByUser(userId, filters)).thenReturn(List.of(goal1, goal2));

        mockMvc.perform(post("/goals/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filters)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Goal 1"))
                .andExpect(jsonPath("$[1].title").value("Goal 2"));

        verify(goalService, times(1)).getGoalsByUser(userId, filters);
    }

    @Test
    public void testCompleteTheGoal() throws Exception {
        Mockito.when(goalService.completeTheGoal(userId, goalId)).thenReturn(setUpGoalDto());
        mockMvc.perform(put("/goals/user/1/goal/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value("Complete the project"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.description").value("Finish the AI project"));
        verify(goalService, times(1)).completeTheGoal(userId, goalId);
    }

    private GoalDto setUpGoalDto() {
        return GoalDto.builder()
                .id(goalId)
                .title("Complete the project")
                .description("Finish the AI project")
                .parentGoalId(null)
                .status(GoalStatus.COMPLETED)
                .skillIds(Collections.singletonList(101L))
                .mentorId(112L)
                .deadline(LocalDateTime.of(2024, 12, 31, 23, 59))
                .createdAt(LocalDateTime.of(2024, 12, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2024, 12, 1, 12, 0))
                .build();
    }
}