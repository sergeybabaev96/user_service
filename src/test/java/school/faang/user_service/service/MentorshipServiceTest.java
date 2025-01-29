package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class MentorshipServiceTest {
    @Mock
    private static MentorshipRepository mentorshipRepository;
    @Mock
    private static UserRepository userRepository;

    @InjectMocks
    private MentorshipService mentorshipService;

    private User mentor;
    private User mentee;
    private Long mentorId;
    private User user;

    @BeforeEach
    void setUp() {
        mentorId = 1L;
        mentor = new User();
        mentor.setId(mentorId);
        mentee = new User();
        mentee.setId(2L);
        mentee.setMentors(Collections.singletonList(mentor));
        Goal goal = new Goal();
        goal.setMentor(mentor);
        mentee.setGoals(Collections.singletonList(goal));
        mentor.setMentees(Collections.singletonList(mentee));
        user = new User();
        user.setId(10L);
        user.setMentees(Collections.singletonList(mentee));
        user.setMentors(List.of(mentor));
    }

    @Test
    void testStopUserMentorship_UserNotFound() {
        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.empty());

        mentorshipService.stopUserMentorship(mentorId);

        verify(mentorshipRepository).findById(mentorId);
        verifyNoMoreInteractions(mentorshipRepository, userRepository);
    }

    @Test
    void testStopUserMentorship() {
        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
        when(userRepository.save(any(User.class))).thenReturn(mentee);

        mentorshipService.stopUserMentorship(mentorId);

        verify(mentorshipRepository, times(1)).findById(mentorId);
        verify(userRepository, times(2)).save(any(User.class));
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
