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
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private MentorshipMapper mentorshipMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    public void testPositiveShouldReturnMenteesList() {

        var userFirst = createUserAndDto(1L, "Filipp");
        var userSecond = createUserAndDto(2L, "Ivan");

        when(mentorshipRepository.findAllMenteesByMentorId(1L))
                .thenReturn(List.of(userFirst.user(), userSecond.user()));
        when(mentorshipMapper.toDto(userFirst.user())).thenReturn(userFirst.mentorDto());
        when(mentorshipMapper.toDto(userSecond.user())).thenReturn(userSecond.mentorDto());

        List<MentorshipDto> result = mentorshipService.getMentees(1L);

        verify(mentorshipRepository).findAllMenteesByMentorId(1L);
        verify(mentorshipMapper).toDto(userFirst.user());
        verify(mentorshipMapper).toDto(userSecond.user());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Filipp", result.get(0).getUsername());
        assertEquals("Ivan", result.get(1).getUsername());
    }

    @Test
    public void testNegativeShouldReturnEmptyListNoMentees() {
        when(mentorshipRepository.findAllMenteesByMentorId(1L))
                .thenReturn(Collections.emptyList());

        List<MentorshipDto> result = mentorshipService.getMentees(1L);

        verify(mentorshipRepository, times(1)).findAllMenteesByMentorId(1L);
        verifyNoInteractions(mentorshipMapper);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testNegativeShouldReturnEmptyListForMenteesWhenRepositoryReturnsNull() {
        when(mentorshipRepository.findAllMenteesByMentorId(1L))
                .thenReturn(null);

        List<MentorshipDto> result = mentorshipService.getMentees(1L);

        verify(mentorshipRepository, times(1)).findAllMenteesByMentorId(1L);
        verifyNoInteractions(mentorshipMapper);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testPositiveShouldReturnMentorsList() {
        var userFirst = createUserAndDto(1L, "Filipp");
        var userSecond = createUserAndDto(2L, "Ivan");

        when(userRepository.findAllById(Collections.singleton(1L)))
                .thenReturn(List.of(userFirst.user(), userSecond.user()));
        when(mentorshipMapper.toDto(userFirst.user())).thenReturn(userFirst.mentorDto());
        when(mentorshipMapper.toDto(userSecond.user())).thenReturn(userSecond.mentorDto());

        List<MentorshipDto> result = mentorshipService.getMentors(1L);

        verify(userRepository, times(1)).findAllById(Collections.singleton(1L));
        verify(mentorshipMapper, times(2)).toDto(any(User.class));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Filipp", result.get(0).getUsername());
        assertEquals("Ivan", result.get(1).getUsername());
    }


    @Test
    void testNegativeShouldReturnEmptyListWhenNoMentors() {
        when(userRepository.findAllById(Collections.singleton(1L)))
                .thenReturn(Collections.emptyList());

        List<MentorshipDto> result = mentorshipService.getMentors(1L);

        verify(userRepository, times(1)).findAllById(Collections.singleton(1L));
        verifyNoInteractions(mentorshipMapper);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testNegativeShouldReturnEmptyListForMentorsWhenRepositoryReturnsNull() {
        when(userRepository.findAllById(Collections.singleton(1L)))
                .thenReturn(null);

        List<MentorshipDto> result = mentorshipService.getMentors(1L);

        verify(userRepository, times(1)).findAllById(Collections.singleton(1L));
        verifyNoInteractions(mentorshipMapper);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testPositiveDeleteMentee_ShouldDeleteMentee_WhenMenteeExists() {
        var user = createUserAndDto(1L, "Filipp");

        when(mentorshipRepository.findAllMenteesByMentorId(1L))
                .thenReturn(Collections.singletonList(user.user()));

        mentorshipService.deleteMentee(1L, 1L);

        verify(mentorshipRepository, times(1)).deleteById(1L);
        assertTrue(true);
    }

    @Test
    void testNegativeDeleteMentee_ShouldThrowException_WhenNoMenteesFound() {
        when(mentorshipRepository.findAllMenteesByMentorId(1L))
                .thenReturn(Collections.emptyList());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                mentorshipService.deleteMentee(1L, 1L)
        );

        verify(mentorshipRepository, never()).deleteById(anyLong());
        assertEquals("У ментора с ID 1 нет ни одного менти", exception.getMessage());
    }

    @Test
    void testPositiveDeleteMentor_ShouldDeleteMentor_WhenMentorExists() {
        var user = createUserAndDto(1L, "Filipp");

        when(userRepository.findAllById(Collections.singleton(2L)))
                .thenReturn(Collections.singletonList(user.user()));
        when(mentorshipMapper.toDto(user.user()))
                .thenReturn(user.mentorDto());

        mentorshipService.deleteMentor(2L, 1L);

        verify(mentorshipRepository, times(1)).deleteById(1L);
        assertTrue(true);
    }

    @Test
    void testNegativeDeleteMentor_ShouldThrowException_WhenNoMentorsFound() {
        when(userRepository.findAllById(Collections.singleton(2L)))
                .thenReturn(Collections.emptyList());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                mentorshipService.deleteMentor(2L, 1L)
        );

        verify(mentorshipRepository, never()).deleteById(anyLong());
        assertEquals("У менти с ID 2 нет менторов", exception.getMessage());
    }

    private UserWithDto createUserAndDto(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);

        MentorshipDto dto = new MentorshipDto();
        dto.setId(id);
        dto.setUsername(username);

        return new UserWithDto(user, dto);
    }

    private record UserWithDto(User user, MentorshipDto mentorDto) {
    }
}
