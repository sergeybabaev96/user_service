package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.mapper.MentorshipMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private MentorshipMapper mentorshipMapper = new MentorshipMapperImpl();

    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    void testGetMenteesSuccess() {
        long userId = 1L;
        User user1 = new User();
        user1.setId(userId);
        User user2 = new User();
        User user3 = new User();
        user2.setId(2L);
        user2.setUsername("mentee1");
        user3.setId(3L);
        user3.setUsername("mentee2");
        List<User> userList = Arrays.asList(user2, user3);
        user1.setMentees(userList);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        List<MentorshipDto> result = mentorshipService.getMentees(userId);

        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getUserId());
        assertEquals("mentee1", result.get(0).getUserName());
        assertEquals("mentee2", result.get(1).getUserName());
    }

    @Test
    public void testGetMenteesUserNotFound() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        List<MentorshipDto> result = mentorshipService.getMentees(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMenteesNoMentees() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setMentees(Collections.emptyList());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<MentorshipDto> result = mentorshipService.getMentees(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testDeleteMenteeSuccess() {
        long menteeId = 1L;
        long mentorId = 2L;
        User mentee = new User();
        User mentor = new User();
        mentee.setId(menteeId);
        mentor.setId(mentorId);
        mentor.setMentees(new ArrayList<>(List.of(mentee)));
        mentee.setMentors(new ArrayList<>(List.of(mentor)));

        when(userRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
        when(userRepository.findById(menteeId)).thenReturn(Optional.of(mentee));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mentorshipService.deleteMentee(menteeId, mentorId);

        assertFalse(mentor.getMentees().contains(mentee));
        assertFalse(mentee.getMentors().contains(mentor));

        verify(userRepository, times(1)).save(mentor);
        verify(userRepository, times(1)).save(mentee);
        verify(userRepository, times(2)).save(any(User.class));
    }

    /*
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException("Mentor not found."));
        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException("Mentee not found."));
        if (!mentor.getMentees().remove(mentee)) {
            throw new EntityNotFoundException("The mentee was not found in the mentor's list.");
        }
        mentee.getMentors().remove(mentor);
        userRepository.save(mentor);
        userRepository.save(mentee);

        public void deleteMentee(long menteeId, long mentorId) {
        deleteMentorship(menteeId, mentorId);
    }
    */

}