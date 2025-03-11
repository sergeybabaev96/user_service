package school.faang.user_service.service.goal.validator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class InvitationDtoValidator {
    public static final String INVITER = "Inviter";
    public static final String INVITED = "Invited";
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    public void validate(final GoalInvitationDto goalInviteDto) {
        validateUserDoesNotInviteHimself(goalInviteDto);
        validateUserExists(goalInviteDto.inviterId(), INVITER);
        validateUserExists(goalInviteDto.invitedUserId(), INVITED);
        validateGoalExists(goalInviteDto.goalId());
    }

    private void validateUserDoesNotInviteHimself(GoalInvitationDto goalInviteDto) {
        if (goalInviteDto.invitedUserId().equals(goalInviteDto.inviterId())) {
            throw new DataValidationException(String.format("The user cannot invite himself! Invited user id: %s",
                    goalInviteDto.invitedUserId()));
        }
    }

    private void validateUserExists(Long userId, String userType) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("%s user with id: %s does not exist.", userType, userId));
        }
    }

    private void validateGoalExists(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new NoSuchElementException(String.format("Goal with id: %s does not exist.", goalId));
        }
    }
}
