package school.faang.user_service.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SuccessResponseDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.MentorshipNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.MentorshipRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
    private MentorshipServiceImpl mentorshipService;

    @Nested
    class TestGetMentees {

        @Test
        void testGetMentees_shouldReturnDtoList_whenMenteesExist() {
            // given
            User mentee = new User();
            mentee.setId(DEFAULT_MENTEE_ID);
            mentee.setUsername("menteeUser");
            mentee.setEmail("mentee@example.com");

            List<User> mentees = List.of(mentee);
            when(mentorshipRepository.findAllMenteesByMentorId(DEFAULT_MENTOR_ID)).thenReturn(mentees);

            UserDto menteeDto = new UserDto(mentee.getId(), mentee.getUsername(), mentee.getEmail());
            when(userMapper.toDto(mentee)).thenReturn(menteeDto);

            // when
            List<UserDto> result = mentorshipService.getMentees(DEFAULT_MENTOR_ID);

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(menteeDto, result.get(0));

            // verify
            verify(mentorshipRepository, times(1)).findAllMenteesByMentorId(DEFAULT_MENTOR_ID);
            verify(userMapper, times(1)).toDto(mentee);
            verifyNoMoreInteractions(mentorshipRepository, userMapper);
        }

        @Test
        void testGetMentees_shouldReturnEmptyList_whenNoMenteesExist() {
            // given
            when(mentorshipRepository.findAllMenteesByMentorId(DEFAULT_MENTOR_ID)).thenReturn(List.of());

            // when
            List<UserDto> result = mentorshipService.getMentees(DEFAULT_MENTOR_ID);

            // then
            assertNotNull(result);
            assertTrue(result.isEmpty());

            // verify
            verify(mentorshipRepository, times(1)).findAllMenteesByMentorId(DEFAULT_MENTOR_ID);
            verifyNoInteractions(userMapper);
            verifyNoMoreInteractions(mentorshipRepository);
        }
    }

    @Nested
    class TestGetMentors {

        @Test
        void testGetMentors_shouldReturnDtoList_whenMentorsExist() {
            // given
            User mentor = new User();
            mentor.setId(ANOTHER_MENTOR_ID);
            mentor.setUsername("mentorUser");
            mentor.setEmail("mentor@example.com");

            List<User> mentors = List.of(mentor);
            when(mentorshipRepository.findAllMentorsByMenteeId(DEFAULT_MENTEE_ID)).thenReturn(mentors);

            UserDto mentorDto = new UserDto(mentor.getId(), mentor.getUsername(), mentor.getEmail());
            when(userMapper.toDto(mentor)).thenReturn(mentorDto);

            // when
            List<UserDto> result = mentorshipService.getMentors(DEFAULT_MENTEE_ID);

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(mentorDto, result.get(0));

            // verify
            verify(mentorshipRepository, times(1)).findAllMentorsByMenteeId(DEFAULT_MENTEE_ID);
            verify(userMapper, times(1)).toDto(mentor);
            verifyNoMoreInteractions(mentorshipRepository, userMapper);
        }

        @Test
        void testGetMentors_shouldReturnEmptyList_whenNoMentorsExist() {
            // given
            when(mentorshipRepository.findAllMentorsByMenteeId(DEFAULT_MENTEE_ID)).thenReturn(List.of());

            // when
            List<UserDto> result = mentorshipService.getMentors(DEFAULT_MENTEE_ID);

            // then
            assertNotNull(result);
            assertTrue(result.isEmpty());

            // verify
            verify(mentorshipRepository, times(1)).findAllMentorsByMenteeId(DEFAULT_MENTEE_ID);
            verifyNoInteractions(userMapper);
            verifyNoMoreInteractions(mentorshipRepository);
        }
    }

    @Nested
    class TestDeleteMentee {

        @Test
        void shouldRemoveRelationshipWhenDeleteMentee() {
            // Arrange
            long mentorId = 1L;
            long menteeId = 2L;
            long mentorshipId = 10L;

            when(mentorshipRepository.findIdByMentorIdAndMenteeId(mentorId, menteeId))
                    .thenReturn(Optional.of(mentorshipId));
            // Act
            SuccessResponseDto response = mentorshipService.deleteMentee(mentorId, menteeId);

            // Assert
            verify(mentorshipRepository).findIdByMentorIdAndMenteeId(mentorId, menteeId);
            verify(mentorshipRepository).deleteById(mentorshipId);
            assertEquals("Mentee with ID 2 successfully deleted from mentor with ID 1", response.message());
        }

        @Test
        void shouldThrowExceptionWhenDeleteMenteeAndRelationshipNotFound() {
            // Arrange
            long mentorId = 1L;
            long menteeId = 2L;

            when(mentorshipRepository.findIdByMentorIdAndMenteeId(mentorId, menteeId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(MentorshipNotFoundException.class, () -> mentorshipService.deleteMentee(mentorId, menteeId));
        }
    }

    @Nested
    class TestDeleteMentor {

        @Test
        void shouldRemoveRelationshipWhenDeleteMentor() {
            // Arrange
            long mentorId = 1L;
            long menteeId = 2L;
            long mentorshipId = 10L;

            when(mentorshipRepository.findIdByMentorIdAndMenteeId(mentorId, menteeId))
                    .thenReturn(Optional.of(mentorshipId));

            // Act
            SuccessResponseDto response = mentorshipService.deleteMentor(mentorId, menteeId);

            // Assert
            verify(mentorshipRepository).findIdByMentorIdAndMenteeId(mentorId, menteeId);
            verify(mentorshipRepository).deleteById(mentorshipId);
            assertEquals("Mentor with ID 1 successfully deleted from mentee with ID 2", response.message());
        }

        @Test
        void shouldThrowExceptionWhenDeleteMentorAndRelationshipNotFound() {
            // Arrange
            long mentorId = 1L;
            long menteeId = 2L;

            when(mentorshipRepository.findIdByMentorIdAndMenteeId(mentorId, menteeId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(MentorshipNotFoundException.class, () -> mentorshipService.deleteMentor(mentorId, menteeId));
        }
    }
}
