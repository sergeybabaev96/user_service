package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.GlobalExceptionHandler;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.service.mentorship.MentorshipService;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {

    @Mock
    private MentorshipService mentorshipService;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private MentorshipController mentorshipController;

    private MockMvc mockMvc;

    private List<UserDto> expectedUsers;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mentorshipController).
                setControllerAdvice(new GlobalExceptionHandler())
                .build();
        List<User> mentees = List.of(
                User.builder().id(1L).username("Hayotbek").build(),
                User.builder().id(2L).username("Ilhan").build(),
                User.builder().id(3L).username("Sergey").build()
        );


        expectedUsers = mentees.stream()
                .map((user -> userMapper.toDto(user))).toList();
    }

    @Test
    void testGetMentee() throws Exception {
        when(mentorshipService.getMentees(1)).thenReturn(expectedUsers);
        mockMvc.perform(get("/api/mentor/1/mentees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].username", is("Hayotbek")))
                .andExpect(jsonPath("$[1].username", is("Ilhan")))
                .andExpect(jsonPath("$[2].username", is("Sergey")));

        verify(userMapper, times(expectedUsers.size())).toDto(any());
        verify(mentorshipService, times(1)).getMentees(1);
    }

    @Test
    void testGetMentors() throws Exception {
        when(mentorshipService.getMentors(1)).thenReturn(expectedUsers);
        mockMvc.perform(get("/api/mentee/1/mentors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].username", is("Hayotbek")))
                .andExpect(jsonPath("$[1].username", is("Ilhan")))
                .andExpect(jsonPath("$[2].username", is("Sergey")));

        verify(userMapper, times(expectedUsers.size())).toDto(any());
        verify(mentorshipService, times(1)).getMentors(1);
    }

    @Test
    void testDeleteMentorAndMentee() throws Exception {
        long mentorId = 1;
        long menteeId = 2;
        doNothing().when(mentorshipService).deleteMenteeAndMentor(menteeId, mentorId);

        mockMvc.perform(delete("/api/mentee/{menteeId}/mentor/{mentorId}", menteeId, mentorId))
                .andExpect(status().isNoContent());

        verify(mentorshipService, times(1)).deleteMenteeAndMentor(menteeId, mentorId);
    }

    @Test
    void testGetMentees_MentorNotFound() throws Exception {
        when(mentorshipService.getMentees(999L))
                .thenThrow(new DataValidationException("Mentor not found"));

        mockMvc.perform(get("/api/mentor/999/mentees"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Mentor not found"));
    }

    @Test
    void testGetMentors_MenteeNotFound() throws Exception {
        when(mentorshipService.getMentors(999L))
                .thenThrow(new DataValidationException("Mentee not found"));

        mockMvc.perform(get("/api/mentee/999/mentors"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Mentee not found"));
    }

    @Test
    void testDeleteMentorAndMentee_RelationNotFound() throws Exception {
        doThrow(new DataValidationException("Mentorship relation not found"))
                .when(mentorshipService).deleteMenteeAndMentor(2, 1);

        mockMvc.perform(delete("/api/mentee/2/mentor/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Mentorship relation not found"));
    }
}


