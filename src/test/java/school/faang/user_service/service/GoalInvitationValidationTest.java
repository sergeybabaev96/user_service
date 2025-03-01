package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.validation.goal.GoalInvitationValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationValidationTest {

    @InjectMocks
    GoalInvitationValidation validation;

    @Mock
    UserService userService;
    @Mock
    GoalInvitationRepository goalInvitationRepository;

    @Test
    public void testCallingExceptionIfIsNotGoal(){
        when(goalInvitationRepository.existsById(1L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> validation.checkAcceptingInvitation(1L));
    }

    @Test
    public void testWhenUserWorkingWithGoal(){
        GoalInvitation goalInvitation = testGoalInvitation(RequestStatus.PENDING);
        when(goalInvitationRepository.existsById(1L)).thenReturn(true);
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));

        assertThrows(BusinessException.class, () -> validation.checkAcceptingInvitation(goalInvitation.getId()));
    }

    @Test
    public void testWhenTheUserMaxGoal(){
        User user = new User();
        user.setId(1L);
        GoalInvitation goalInvitation = testGoalInvitation(RequestStatus.REJECTED);
        goalInvitation.setInvited(user);
        List<GoalInvitation> listActiveGoalUser = List.of(goalInvitation, goalInvitation);

        when(goalInvitationRepository.existsById(1L)).thenReturn(true);
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));

        when(userService.getUserById(anyLong())).thenReturn(user);
        when(goalInvitationRepository.findAll()).thenReturn(listActiveGoalUser);

        assertThrows(BusinessException.class, () -> validation.checkAcceptingInvitation(goalInvitation.getId()));
    }

    @Test
    public void testWhenTheInviterIncorrectId(){
        GoalInvitationDto dto = testGoalInvitationDto(0, 2);
        assertThrows(BusinessException.class, () -> validation.checkInvitation(dto));
    }

    @Test
    public void testWhenTheInvitedIncorrectId(){
        GoalInvitationDto dto = testGoalInvitationDto(2, 0);
        assertThrows(BusinessException.class, () -> validation.checkInvitation(dto));
    }

    @Test
    public void testWhenInvitedAndInviterHaveOneId(){
        GoalInvitationDto dto = testGoalInvitationDto(3, 3);
        assertThrows(BusinessException.class, () -> validation.checkInvitation(dto));
    }

    private GoalInvitationDto testGoalInvitationDto(long idInviter, long idInvited){
        GoalInvitationDto dto = new GoalInvitationDto();
        dto.setInviterId(idInviter);
        dto.setInvitedUserId(idInvited);

        return dto;
    }

    private GoalInvitation testGoalInvitation(RequestStatus status){
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);

        goalInvitation.setStatus(status);

        return goalInvitation;
    }

}
