package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @InjectMocks
    private MentorshipService mentorshipService;

    @Mock
    private MentorshipRepository mentorshipRepository;

    private User user;
    private User mentee;
    private User mentor;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("user");

        mentee = new User();
        mentee.setId(2L);
        mentee.setUsername("mentee");

        mentor = new User();
        mentor.setId(3L);
        mentor.setUsername("mentor");

        user.setMentees(Collections.singletonList(mentee));
        user.setMentors(List.of(mentor));
    }

    @Test
    void testGetMentees() {
        when(mentorshipRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(mentorshipRepository.findAllById(List.of(mentee.getId()))).thenReturn(List.of(mentee));

        List<Long> result = mentorshipService.getMentees(user.getId());

        assertEquals(List.of(mentee.getId()), result);
    }

    @Test
    void testGetMentees_UserNotFound() {
        when(mentorshipRepository.findById(user.getId())).thenReturn(Optional.empty());

        List<Long> result = mentorshipService.getMentees(user.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetMentors() {
        when(mentorshipRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(mentorshipRepository.findAllById(List.of(mentor.getId()))).thenReturn(List.of(mentor));

        List<Long> result = mentorshipService.getMentors(user.getId());

        assertEquals(List.of(mentor.getId()), result);
    }

    @Test
    void testGetMentors_UserNotFound() {
        when(mentorshipRepository.findById(user.getId())).thenReturn(Optional.empty());

        List<Long> result = mentorshipService.getMentors(user.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteMentee_MentorExistsAndMenteeExists() {
        when(mentorshipRepository.findById(user.getId())).thenReturn(Optional.of(user));

        mentorshipService.deleteMentee(user.getId(), mentee.getId());

        verify(mentorshipRepository, Mockito.times(1)).delete(mentee);
    }

    @Test
    void testDeleteMentee_MentorNotFound() {
        when(mentorshipRepository.findById(user.getId())).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> mentorshipService.deleteMentee(user.getId(), mentee.getId()));
    }

    @Test
    void testDeleteMentee_MenteeNotFound() {
        when(mentorshipRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(NoSuchElementException.class, () -> mentorshipService.deleteMentee(user.getId(), 999L));
    }

    @Test
    void testDeleteMentor_MentorExistsAndMenteeExists() {
        when(mentorshipRepository.findById(user.getId())).thenReturn(Optional.of(user));

        mentorshipService.deleteMentor(mentor.getId(), user.getId());

        verify(mentorshipRepository, Mockito.times(1)).delete(mentor);
    }

    @Test
    void testDeleteMentor_MenteeNotFound() {
        when(mentorshipRepository.findById(user.getId())).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> mentorshipService.deleteMentor(mentor.getId(), user.getId()));
    }

    @Test
    void testDeleteMentor_MentorNotFound() {
        when(mentorshipRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(NoSuchElementException.class, () -> mentorshipService.deleteMentor(999L, user.getId()));
    }
}
