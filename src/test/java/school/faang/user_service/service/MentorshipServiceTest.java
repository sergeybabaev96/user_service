package school.faang.user_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    private static final Long DEFAULT_MENTOR_ID = 1L;
    private static final Long DEFAULT_MENTEE_ID = 2L;
    private static final Long ANOTHER_MENTOR_ID = 3L;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    @Nested
    class TestGetMentees {

        @Test
        void testGetMentees_shouldReturnDtoList_whenMenteesExist() {
            User mentee = new User();
            mentee.setId(DEFAULT_MENTEE_ID);
            mentee.setUsername("menteeUser");
            mentee.setEmail("mentee@example.com");

            List<User> mentees = List.of(mentee);
            when(mentorshipRepository.findAllMenteesByMentorId(DEFAULT_MENTOR_ID)).thenReturn(mentees);

            UserDto menteeDto = new UserDto(mentee.getId(), mentee.getUsername(), mentee.getEmail());
            when(userMapper.toDto(mentee)).thenReturn(menteeDto);

            List<UserDto> result = mentorshipService.getMentees(DEFAULT_MENTOR_ID);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(menteeDto, result.get(0));
        }

        @Test
        void testGetMentees_shouldReturnEmptyList_whenNoMenteesExist() {
            when(mentorshipRepository.findAllMenteesByMentorId(DEFAULT_MENTOR_ID)).thenReturn(List.of());

            List<UserDto> result = mentorshipService.getMentees(DEFAULT_MENTOR_ID);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class TestGetMentors {

        @Test
        void testGetMentors_shouldReturnDtoList_whenMentorsExist() {
            User mentor = new User();
            mentor.setId(ANOTHER_MENTOR_ID);
            mentor.setUsername("mentorUser");
            mentor.setEmail("mentor@example.com");

            List<User> mentors = List.of(mentor);
            when(mentorshipRepository.findAllMentorsByMenteeId(DEFAULT_MENTEE_ID)).thenReturn(mentors);

            UserDto mentorDto = new UserDto(mentor.getId(), mentor.getUsername(), mentor.getEmail());
            when(userMapper.toDto(mentor)).thenReturn(mentorDto);

            List<UserDto> result = mentorshipService.getMentors(DEFAULT_MENTEE_ID);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(mentorDto, result.get(0));
        }

        @Test
        void testGetMentors_shouldReturnEmptyList_whenNoMentorsExist() {
            when(mentorshipRepository.findAllMentorsByMenteeId(DEFAULT_MENTEE_ID)).thenReturn(List.of());

            List<UserDto> result = mentorshipService.getMentors(DEFAULT_MENTEE_ID);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class TestDeleteMentee {

        @Test
        void testDeleteMentee_shouldRemoveRelationship_whenExists() {
            User mentor = new User();
            mentor.setId(DEFAULT_MENTOR_ID);

            User mentee = new User();
            mentee.setId(DEFAULT_MENTEE_ID);

            List<User> mentees = new ArrayList<>();
            mentees.add(mentee);
            mentor.setMentees(mentees);

            when(mentorshipRepository.findById(DEFAULT_MENTOR_ID)).thenReturn(Optional.of(mentor));
            when(mentorshipRepository.save(mentor)).thenReturn(mentor);

            assertDoesNotThrow(() -> mentorshipService.deleteMentee(DEFAULT_MENTEE_ID, DEFAULT_MENTOR_ID));
            assertTrue(mentor.getMentees().isEmpty());
        }

        @Test
        void testDeleteMentee_shouldThrowException_whenMentorNotFound() {
            when(mentorshipRepository.findById(DEFAULT_MENTOR_ID)).thenReturn(Optional.empty());

            MentorshipNotFoundException ex = assertThrows(MentorshipNotFoundException.class,
                    () -> mentorshipService.deleteMentee(DEFAULT_MENTEE_ID, DEFAULT_MENTOR_ID));
            assertEquals("Mentor not found: " + DEFAULT_MENTOR_ID, ex.getMessage());
        }

        @Test
        void testDeleteMentee_shouldThrowException_whenRelationshipNotFound() {
            User mentor = new User();
            mentor.setId(DEFAULT_MENTOR_ID);
            mentor.setMentees(new ArrayList<>());

            when(mentorshipRepository.findById(DEFAULT_MENTOR_ID)).thenReturn(Optional.of(mentor));

            MentorshipNotFoundException ex = assertThrows(MentorshipNotFoundException.class,
                    () -> mentorshipService.deleteMentee(DEFAULT_MENTEE_ID, DEFAULT_MENTOR_ID));
            assertEquals("No mentorship relationship found for mentor "
                    + DEFAULT_MENTOR_ID + " and mentee " + DEFAULT_MENTEE_ID, ex.getMessage());
        }
    }

    @Nested
    class TestDeleteMentor {

        @Test
        void testDeleteMentor_shouldRemoveRelationship_whenExists() {
            User mentee = new User();
            mentee.setId(DEFAULT_MENTEE_ID);

            User mentor = new User();
            mentor.setId(ANOTHER_MENTOR_ID);

            List<User> mentors = new ArrayList<>();
            mentors.add(mentor);
            mentee.setMentors(mentors);

            when(mentorshipRepository.findById(DEFAULT_MENTEE_ID)).thenReturn(Optional.of(mentee));
            when(mentorshipRepository.save(mentee)).thenReturn(mentee);

            assertDoesNotThrow(() -> mentorshipService.deleteMentor(DEFAULT_MENTEE_ID, ANOTHER_MENTOR_ID));
            assertTrue(mentee.getMentors().isEmpty());
        }

        @Test
        void testDeleteMentor_shouldThrowException_whenMenteeNotFound() {
            when(mentorshipRepository.findById(DEFAULT_MENTEE_ID)).thenReturn(Optional.empty());

            MentorshipNotFoundException ex = assertThrows(MentorshipNotFoundException.class,
                    () -> mentorshipService.deleteMentor(DEFAULT_MENTEE_ID, ANOTHER_MENTOR_ID));
            assertEquals("Mentee not found: " + DEFAULT_MENTEE_ID, ex.getMessage());
        }

        @Test
        void testDeleteMentor_shouldThrowException_whenRelationshipNotFound() {
            User mentee = new User();
            mentee.setId(DEFAULT_MENTEE_ID);
            mentee.setMentors(new ArrayList<>());

            when(mentorshipRepository.findById(DEFAULT_MENTEE_ID)).thenReturn(Optional.of(mentee));

            MentorshipNotFoundException ex = assertThrows(MentorshipNotFoundException.class,
                    () -> mentorshipService.deleteMentor(DEFAULT_MENTEE_ID, ANOTHER_MENTOR_ID));
            assertEquals("No mentorship relationship found for mentee "
                    + DEFAULT_MENTEE_ID + " and mentor " + ANOTHER_MENTOR_ID, ex.getMessage());
        }
    }
}
