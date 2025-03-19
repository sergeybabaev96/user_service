package school.faang.user_service.controller.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.service.MentorshipService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MentorshipController.class)
public class MentorshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MentorshipService mentorshipService;

    @MockBean
    private UserContext userContext;

    @Test
    public void testGetMenteesSuccess() throws Exception {
        long mentorId = 1L;
        MentorshipDto mentorshipDto1 = new MentorshipDto();
        mentorshipDto1.setUserId(2L);
        mentorshipDto1.setUserName("mentee1");
        MentorshipDto mentorshipDto2 = new MentorshipDto();
        mentorshipDto2.setUserId(3L);
        mentorshipDto2.setUserName("mentee2");
        List<MentorshipDto> mentees = Arrays.asList(mentorshipDto1, mentorshipDto2);

        when(mentorshipService.getMentees(mentorId)).thenReturn(mentees);

        mockMvc.perform(get("/mentorship/mentors/{mentorId}/mentees", mentorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value(2L))
                .andExpect(jsonPath("$[0].userName").value("mentee1"))
                .andExpect(jsonPath("$[1].userId").value(3L))
                .andExpect(jsonPath("$[1].userName").value("mentee2"));
    }

    @Test
    public void testGetMenteesEmptyList() throws Exception {
        long mentorId = 1L;
        when(mentorshipService.getMentees(mentorId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/mentorship/mentors/{mentorId}/mentees", mentorId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void deleteMenteeSuccessfulDeletionReturnsNoContent() throws Exception {
        long mentorId = 1L;
        long menteeId = 2L;
        doNothing().when(mentorshipService).deleteMentee(menteeId, mentorId);

        mockMvc.perform(delete("/mentorship/mentors/{mentorId}/mentees/{menteeId}", mentorId, menteeId))
                .andExpect(status().isNoContent());

        verify(mentorshipService).deleteMentee(menteeId, mentorId);
    }


    @Test
    public void deleteMenteeWhenServiceThrowsException() {
        long mentorId = 1L;
        long menteeId = 2L;

        doThrow(new EntityNotFoundException("Mentor not found"))
                .when(mentorshipService).deleteMentee(menteeId, mentorId);

        try {
            mockMvc.perform(delete("/mentorship/mentors/{mentorId}/mentees/{menteeId}", mentorId, menteeId));
        } catch (Exception e) {
            assertInstanceOf(EntityNotFoundException.class, e.getCause());
            assertEquals("Mentor not found", e.getCause().getMessage());
        }

        verify(mentorshipService).deleteMentee(menteeId, mentorId);
    }

}