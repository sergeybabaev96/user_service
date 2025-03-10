package school.faang.user_service.service.mentorship.validator;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.validator.InvitationDtoValidator;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InvitationDtoValidatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private InvitationDtoValidator invitationDtoValidator;

    private GoalInvitationDto validGoalInvitationDto;

    @BeforeEach
    void setUp() {
        validGoalInvitationDto = new GoalInvitationDto(1L, 2L, 1L);
    }

    @Test
    void testValidateSuccessfulValidation() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(goalRepository.existsById(1L)).thenReturn(true);

        invitationDtoValidator.validate(validGoalInvitationDto);
    }

    @Test
    void testValidateUserInvitesSelfThrowsException() {
        validGoalInvitationDto = new GoalInvitationDto(1L, 1L, 1L);

        assertThrows(DataValidationException.class, () -> invitationDtoValidator.validate(validGoalInvitationDto));
    }

    @Test
    void testValidateUserDoesNotInviteHimselfShouldThrowExceptionWhenUserInvitesHimself() {
        validGoalInvitationDto = new GoalInvitationDto(1L, 1L, 1L);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> invitationDtoValidator.validate(validGoalInvitationDto));

        assertEquals(String.format("The user cannot invite himself! Invited user id: %s",
                validGoalInvitationDto.invitedUserId()), exception.getMessage());
    }

    @Test
    void testValidateUserExistsShouldThrowEntityNotFoundExceptionWhenUserNotFound() {
        Long userId = 1L;
        String userType = "Inviter";

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> invitationDtoValidator.validate(validGoalInvitationDto)
        );
        assertEquals(String.format("%s user with id: %s does not exist.", userType, userId), exception.getMessage());
    }

    @Test
    void testValidateGoalExistsShouldThrowNoSuchElementExceptionWhenGoalNotFound() {
        Long goalId = 1L;

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(goalRepository.existsById(goalId)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> invitationDtoValidator.validate(validGoalInvitationDto)
        );
        assertEquals(String.format("Goal with id: %s does not exist.", goalId), exception.getMessage());
    }

    @Test
    void testValidateGoalDoesNotExistThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(goalRepository.existsById(1L)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> invitationDtoValidator.validate(validGoalInvitationDto));
    }
}
