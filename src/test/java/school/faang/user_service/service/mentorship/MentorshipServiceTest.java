package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.mentorship.MentorshipController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private MentorshipController mentorshipController;

    private List<UserDto> users = null;
    private List<UserDto> mentees = new ArrayList<>();
    private List<UserDto> mentors = new ArrayList<>();

    @BeforeEach
    void setUp() {
        users = Arrays.asList(
                UserDto.builder().id(0L).username("user0").build(),
                UserDto.builder().id(1L).username("user1").build(),
                UserDto.builder().id(2L).username("user2").build(),
                UserDto.builder().id(3L).username("user3").build(),
                UserDto.builder().id(4L).username("user4").build(),
                UserDto.builder().id(5L).username("user5").build(),
                UserDto.builder().id(6L).username("user6").build(),
                UserDto.builder().id(7L).username("user7").build(),
                UserDto.builder().id(8L).username("user8").build(),
                UserDto.builder().id(9L).username("user9").build()
        );

        mentees.addAll(Arrays.asList(
                users.get(9), users.get(8), users.get(7), users.get(6)
        ));

        mentors.addAll(Arrays.asList(
                users.get(1), users.get(2)
        ));

        users.get(3).setMentees(mentees);
        users.get(5).setMentees(mentees);

        users.get(4).setMentors(mentors);
    }

    @Test
    void testGetMentees() {
        when(mentorshipService.getMentees(3L)).thenReturn(mentees);

        List<UserDto> response = mentorshipController.getMentees(3);

        assertEquals(4, response.size());
    }

    @Test
    void testGetMentors() {
        when(mentorshipService.getMentors(4L)).thenReturn(mentors);

        List<UserDto> response = mentorshipController.getMentors(4);

        assertEquals(2, response.size());
    }

    @Test
    void testDeleteMentee() {
        mentorshipService.deleteMentee(5L, 3L);

        mentees.remove(3);

        when(mentorshipService.getMentees(3L)).thenReturn(mentees);

        List<UserDto> response = mentorshipController.getMentees(3);

        assertEquals(3, response.size());
    }

    @Test
    void testDeleteMentor() {
        mentorshipService.deleteMentor(4L, 1L);

        mentors.remove(1);

        when(mentorshipService.getMentors(4L)).thenReturn(mentors);

        List<UserDto> response = mentorshipController.getMentors(4);

        assertEquals(1, response.size());
    }

}