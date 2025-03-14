package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.service.MentorshipService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = MentorshipController.class)
public class MentorshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MentorshipService mentorshipService;

    @MockBean
    private school.faang.user_service.config.context.UserContext userContext;

    private MentorshipControllerTest() {
    }

    public static MentorshipControllerTest createMentorshipControllerTest() {
        return new MentorshipControllerTest();
    }

    @Test
    public void testGetMentees_Success() throws Exception {
        long mentorId = 1L;
        MentorshipDto mentorshipDto1 = new MentorshipDto();
        mentorshipDto1.setUserId(2L);
        mentorshipDto1.setUserName("mentee1");
        MentorshipDto mentorshipDto2 = new MentorshipDto();
        mentorshipDto1.setUserId(3L);
        mentorshipDto1.setUserName("mentee2");
        List<MentorshipDto> mentees = Arrays.asList(mentorshipDto1, mentorshipDto2);
        when(mentorshipService.getMentees(mentorId)).thenReturn(mentees);
        
        mockMvc.perform(get("/mentorship/mentors/{mentorId}/mentees", mentorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(2L))
                .andExpect(jsonPath("$[0].userName").value("mentee1"));

        // Act & Assert: Отправляем GET-запрос и проверяем ответ
        /*mockMvc.perform(get("/mentorship/mentors/{mentorId}/mentees", mentorId))
                .andExpect(status().isOk())                     // Проверяем статус 200 OK
                .andExpect(jsonPath("$").isArray())             // Проверяем, что ответ — массив
                .andExpect(jsonPath("$.length()").value(2))     // Проверяем длину массива
                .andExpect(jsonPath("$[0].userId").value(2L))   // Проверяем первый элемент
                .andExpect(jsonPath("$[0].userName").value("mentee1"))
                .andExpect(jsonPath("$[1].userId").value(3L))   // Проверяем второй элемент
                .andExpect(jsonPath("$[1].userName").value("mentee2"));*/
    }
}

/*        long mentorId = 1L;
        MentorshipDto mentorshipDto1 = new MentorshipDto();
        mentorshipDto1.setUserId(2L);
        mentorshipDto1.setUserName("mentee1");
        MentorshipDto mentorshipDto2 = new MentorshipDto();
        mentorshipDto1.setUserId(3L);
        mentorshipDto1.setUserName("mentee2");
        List<MentorshipDto> mentees = Arrays.asList(mentorshipDto1, mentorshipDto2);
        when(mentorshipService.getMentees(mentorId)).thenReturn(mentees);
        */