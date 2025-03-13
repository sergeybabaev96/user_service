package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
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

    private User mentee1;
    private User mentee2;
    private MentorshipDto mentor1;
    private MentorshipDto mentor2;

    @BeforeEach
    void setUp() {
        mentee1 = new User();
        mentee1.setId(1L);
        mentee1.setUsername("Filipp");
        mentee2 = new User();
        mentee2.setId(2L);
        mentee2.setUsername("Ivan");
        mentor1 = new MentorshipDto();
        mentor1.setId(1L);
        mentor1.setUsername("Filipp");
        mentor2 = new MentorshipDto();
        mentor2.setId(2L);
        mentor2.setUsername("Ivan");
    }

        @Test
        public void shouldReturnMenteesList() {
            when(mentorshipRepository.findAllMenteesByMentorId(1L))
                    .thenReturn(List.of(mentee1, mentee2));
            when(mentorshipMapper.toDto(mentee1)).thenReturn(mentor1);
            when(mentorshipMapper.toDto(mentee2)).thenReturn(mentor2);

            List<MentorshipDto> result = mentorshipService.getMentees(1L);

            verify(mentorshipRepository, times(1)).findAllMenteesByMentorId(1L);
            verify(mentorshipMapper, times(1)).toDto(mentee1);

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Filipp", result.get(0).getUsername());
            assertEquals("Ivan", result.get(1).getUsername());
        }

        @Test
        public void shouldReturnEmptyListNoMentees() {
            when(mentorshipRepository.findAllMenteesByMentorId(1L))
                    .thenReturn(Collections.emptyList());

            List<MentorshipDto> result = mentorshipService.getMentees(1L);

            verify(mentorshipRepository, times(1)).findAllMenteesByMentorId(1L);
            verifyNoInteractions(mentorshipMapper);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void shouldReturnEmptyListForMenteesWhenRepositoryReturnsNull() {
            when(mentorshipRepository.findAllMenteesByMentorId(1L))
                    .thenReturn(null);

            List<MentorshipDto> result = mentorshipService.getMentees(1L);

            verify(mentorshipRepository, times(1)).findAllMenteesByMentorId(1L);
            verifyNoInteractions(mentorshipMapper);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void shouldReturnMentorsList() {

            when(userRepository.findAllById(Collections.singleton(1L)))
                    .thenReturn(List.of(mentee1, mentee2));
            when(mentorshipMapper.toDto(mentee1)).thenReturn(mentor1);
            when(mentorshipMapper.toDto(mentee2)).thenReturn(mentor2);

            List<MentorshipDto> result = mentorshipService.getMentors(1L);

            verify(userRepository, times(1)).findAllById(Collections.singleton(1L));
            verify(mentorshipMapper, times(2)).toDto(any(User.class));

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Filipp", result.get(0).getUsername());
            assertEquals("Ivan", result.get(1).getUsername());
        }


        @Test
        void shouldReturnEmptyListWhenNoMentors() {

            when(userRepository.findAllById(Collections.singleton(1L)))
                    .thenReturn(Collections.emptyList());

            List<MentorshipDto> result = mentorshipService.getMentors(1L);

            verify(userRepository, times(1)).findAllById(Collections.singleton(1L));
            verifyNoInteractions(mentorshipMapper);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void shouldReturnEmptyListForMentorsWhenRepositoryReturnsNull() {

            when(userRepository.findAllById(Collections.singleton(1L)))
                    .thenReturn(null);

            List<MentorshipDto> result = mentorshipService.getMentors(1L);

            verify(userRepository, times(1)).findAllById(Collections.singleton(1L));
            verifyNoInteractions(mentorshipMapper);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
}
