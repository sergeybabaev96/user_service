package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    private static final long MENTOR_ID = 1L;
    private static final long MENTEE_ID = 2L;
    private static final String MENTOR_NOT_FOUND_MESSAGE = String.format("Mentor with id %d not found", MENTOR_ID);
    private static final String MENTEE_NOT_FOUND_MESSAGE = String.format("Mentee with id %d not found", MENTEE_ID);
    private static final String MENTORSHIP_NOT_FOUND_MESSAGE = "Mentorship between mentor 1 and mentee 2 not found";

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private MentorshipMapper mentorshipMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    private User mentor;
    private User mentee;
    private MentorshipDto mentorDto;
    private MentorshipDto menteeDto;

    @BeforeEach
    public void setUp() {
        mentor = User.builder().id(MENTOR_ID).build();
        mentee = User.builder().id(MENTEE_ID).build();

        mentor.setMentees(Collections.singletonList(mentee));
        mentee.setMentors(Collections.singletonList(mentor));

        mentorDto = MentorshipDto.builder().id(MENTOR_ID).build();
        menteeDto = MentorshipDto.builder().id(MENTEE_ID).build();
    }

    @Test
    public void testGetMentees_success() {
        Mockito.when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.of(mentor));
        Mockito.when(mentorshipMapper.toDto(mentee)).thenReturn(menteeDto);

        List<MentorshipDto> result = mentorshipService.getMentees(MENTOR_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(MENTEE_ID, result.get(0).getId());

        Mockito.verify(userRepository).findById(MENTOR_ID);
        Mockito.verify(mentorshipMapper).toDto(mentee);
    }

    @Test
    public void testGetMentees_mentorNotFound() {
        Mockito.when(userRepository.findById(MENTOR_ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                mentorshipService.getMentees(MENTOR_ID)
        );
        assertTrue(ex.getMessage().contains(MENTOR_NOT_FOUND_MESSAGE));

        Mockito.verify(userRepository).findById(MENTOR_ID);
    }

    @Test
    public void testGetMentors_success() {
        Mockito.when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.of(mentee));
        Mockito.when(mentorshipMapper.toDto(mentor)).thenReturn(mentorDto);

        List<MentorshipDto> result = mentorshipService.getMentors(MENTEE_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(MENTOR_ID, result.get(0).getId());

        Mockito.verify(userRepository).findById(MENTEE_ID);
        Mockito.verify(mentorshipMapper).toDto(mentor);
    }

    @Test
    public void testGetMentors_menteeNotFound() {
        Mockito.when(userRepository.findById(MENTEE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                mentorshipService.getMentors(MENTEE_ID)
        );
        assertTrue(ex.getMessage().contains(MENTEE_NOT_FOUND_MESSAGE));

        Mockito.verify(userRepository).findById(MENTEE_ID);
    }

    @Test
    public void testDeleteByMentorIdAndMenteeId_success() {
        Mockito.when(mentorshipRepository.existsByMentorIdAndMenteeId(MENTOR_ID, MENTEE_ID)).thenReturn(true);

        assertDoesNotThrow(() -> mentorshipService.deleteByMentorIdAndMenteeId(MENTOR_ID, MENTEE_ID));

        Mockito.verify(mentorshipRepository).existsByMentorIdAndMenteeId(MENTOR_ID, MENTEE_ID);
        Mockito.verify(mentorshipRepository).deleteByMentorIdAndMenteeId(MENTOR_ID, MENTEE_ID);
    }

    @Test
    public void testDeleteByMentorIdAndMenteeId_notFound() {
        Mockito.when(mentorshipRepository.existsByMentorIdAndMenteeId(MENTOR_ID, MENTEE_ID)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                mentorshipService.deleteByMentorIdAndMenteeId(MENTOR_ID, MENTEE_ID)
        );
        assertTrue(ex.getMessage().contains(MENTORSHIP_NOT_FOUND_MESSAGE));

        Mockito.verify(mentorshipRepository).existsByMentorIdAndMenteeId(MENTOR_ID, MENTEE_ID);
        Mockito.verify(mentorshipRepository, Mockito.never()).deleteByMentorIdAndMenteeId(MENTOR_ID, MENTEE_ID);
    }

}