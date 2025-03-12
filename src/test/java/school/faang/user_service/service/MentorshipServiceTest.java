package school.faang.user_service.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.MentorshipNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    @Nested
    class GetMenteesTests {

        @Test
        void getMentees_shouldReturnDtoList_whenMenteesExist() {
            Long mentorId = 1L;
            User mentee = new User();
            mentee.setId(2L);
            mentee.setUsername("menteeUser");
            mentee.setEmail("mentee@example.com");

            List<User> mentees = List.of(mentee);
            when(mentorshipRepository.findAllMenteesByMentorId(mentorId)).thenReturn(mentees);

            UserDto menteeDto = new UserDto(mentee.getId(), mentee.getUsername(), mentee.getEmail());
            when(userMapper.toDto(mentee)).thenReturn(menteeDto);

            List<UserDto> result = mentorshipService.getMentees(mentorId);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(menteeDto, result.get(0));
        }

        @Test
        void getMentees_shouldReturnEmptyList_whenNoMenteesExist() {
            Long mentorId = 2L;
            when(mentorshipRepository.findAllMenteesByMentorId(mentorId)).thenReturn(List.of());

            List<UserDto> result = mentorshipService.getMentees(mentorId);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class GetMentorsTests {

        @Test
        void getMentors_shouldReturnDtoList_whenMentorsExist() {
            Long menteeId = 1L;
            User mentor = new User();
            mentor.setId(3L);
            mentor.setUsername("mentorUser");
            mentor.setEmail("mentor@example.com");

            List<User> mentors = List.of(mentor);
            when(mentorshipRepository.findAllMentorsByMenteeId(menteeId)).thenReturn(mentors);

            UserDto mentorDto = new UserDto(mentor.getId(), mentor.getUsername(), mentor.getEmail());
            when(userMapper.toDto(mentor)).thenReturn(mentorDto);

            List<UserDto> result = mentorshipService.getMentors(menteeId);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(mentorDto, result.get(0));
        }

        @Test
        void getMentors_shouldReturnEmptyList_whenNoMentorsExist() {
            Long menteeId = 2L;
            when(mentorshipRepository.findAllMentorsByMenteeId(menteeId)).thenReturn(List.of());

            List<UserDto> result = mentorshipService.getMentors(menteeId);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class DeleteMenteeTests {

        @Test
        void deleteMentee_shouldRemoveRelationship_whenExists() {
            Long mentorId = 1L;
            Long menteeId = 2L;

            User mentor = new User();
            mentor.setId(mentorId);
            List<User> mentees = new ArrayList<>();
            User mentee = new User();
            mentee.setId(menteeId);
            mentees.add(mentee);
            mentor.setMentees(mentees);

            when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
            when(mentorshipRepository.save(mentor)).thenReturn(mentor);

            assertDoesNotThrow(() -> mentorshipService.deleteMentee(menteeId, mentorId));
            assertTrue(mentor.getMentees().isEmpty());
        }

        @Test
        void deleteMentee_shouldThrowException_whenMentorNotFound() {
            Long mentorId = 1L;
            Long menteeId = 2L;

            when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.empty());

            MentorshipNotFoundException ex = assertThrows(MentorshipNotFoundException.class,
                    () -> mentorshipService.deleteMentee(menteeId, mentorId));
            assertEquals("Mentor not found: " + mentorId, ex.getMessage());
        }

        @Test
        void deleteMentee_shouldThrowException_whenRelationshipNotFound() {
            Long mentorId = 1L;
            Long menteeId = 2L;

            User mentor = new User();
            mentor.setId(mentorId);
            mentor.setMentees(new ArrayList<>());

            when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.of(mentor));

            MentorshipNotFoundException ex = assertThrows(MentorshipNotFoundException.class,
                    () -> mentorshipService.deleteMentee(menteeId, mentorId));
            assertEquals("No mentorship relationship found for mentor "
                    + mentorId + " and mentee " + menteeId, ex.getMessage());
        }
    }

    @Nested
    class DeleteMentorTests {

        @Test
        void deleteMentor_shouldRemoveRelationship_whenExists() {
            Long menteeId = 1L;
            Long mentorId = 3L;

            User mentee = new User();
            mentee.setId(menteeId);
            List<User> mentors = new ArrayList<>();
            User mentor = new User();
            mentor.setId(mentorId);
            mentors.add(mentor);
            mentee.setMentors(mentors);

            when(mentorshipRepository.findById(menteeId)).thenReturn(Optional.of(mentee));
            when(mentorshipRepository.save(mentee)).thenReturn(mentee);

            assertDoesNotThrow(() -> mentorshipService.deleteMentor(menteeId, mentorId));
            assertTrue(mentee.getMentors().isEmpty());
        }

        @Test
        void deleteMentor_shouldThrowException_whenMenteeNotFound() {
            Long menteeId = 1L;
            Long mentorId = 3L;

            when(mentorshipRepository.findById(menteeId)).thenReturn(Optional.empty());

            MentorshipNotFoundException ex = assertThrows(MentorshipNotFoundException.class,
                    () -> mentorshipService.deleteMentor(menteeId, mentorId));
            assertEquals("Mentee not found: " + menteeId, ex.getMessage());
        }

        @Test
        void deleteMentor_shouldThrowException_whenRelationshipNotFound() {
            Long menteeId = 1L;
            Long mentorId = 3L;

            User mentee = new User();
            mentee.setId(menteeId);
            mentee.setMentors(new ArrayList<>());

            when(mentorshipRepository.findById(menteeId)).thenReturn(Optional.of(mentee));

            MentorshipNotFoundException ex = assertThrows(MentorshipNotFoundException.class,
                    () -> mentorshipService.deleteMentor(menteeId, mentorId));
            assertEquals("No mentorship relationship found for mentee "
                    + menteeId + " and mentor " + mentorId, ex.getMessage());
        }
    }
}
