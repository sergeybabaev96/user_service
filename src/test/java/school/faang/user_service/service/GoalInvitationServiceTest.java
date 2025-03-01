package school.faang.user_service.service;
 
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.GoalInvitationFilter;
import school.faang.user_service.mapper.GoalInvitationMapperImpl;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.validation.goal.GoalInvitationValidation;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private GoalInvitationValidation validation;
    @Mock
    private List<GoalInvitationFilter> invitationFilter;
    @Mock
    private UserService userService;
    @Mock
    private GoalService goalService;
    @Spy
    private GoalInvitationMapperImpl goalInvitationMapper;

    @Captor
    private ArgumentCaptor<GoalInvitation> goalInvitationCaptor;

    @Test
    public void testCreateNewInvitation(){
        GoalInvitationDto dto = new GoalInvitationDto();
        dto.setId(1L);
        dto.setGoalId(4L);
        dto.setInviterId(3L);
        dto.setInvitedUserId(2L);

        Goal goal = new Goal();
        goal.setId(4L);

        User inviter = new User();
        inviter.setId(3L);

        User invited = new User();
        invited.setId(2L);

        doNothing().when(validation).checkInvitation(dto);
        when(goalService.getGoalById(anyLong())).thenReturn(goal);
        when(userService.getUserById(3L)).thenReturn(inviter);
        when(userService.getUserById(2L)).thenReturn(invited);

        GoalInvitationDto result = goalInvitationService.createInvitation(dto);

        verify(goalInvitationRepository, times(1)).save(goalInvitationCaptor.capture());

        GoalInvitation captureGoalInvitation = goalInvitationCaptor.getValue();

        assertEquals(goal.getId(), captureGoalInvitation.getGoal().getId());
        assertEquals(inviter.getId(), captureGoalInvitation.getInviter().getId());
        assertEquals(invited.getId(), captureGoalInvitation.getInvited().getId());

        assertEquals(dto.getGoalId(), result.getGoalId());
        assertEquals(dto.getInviterId(), result.getInviterId());
        assertEquals(dto.getInvitedUserId(), result.getInvitedUserId());
    }

    @Test
    public void testInvitationGoalAccept(){
        GoalInvitation goalInvitation = createTestGoalInvitation();

        checkGoalInvitationStratus(goalInvitation, RequestStatus.ACCEPTED);
    }

    @Test
    public void testInvitationGoalRejected(){
        GoalInvitation goalInvitation = createTestGoalInvitation();

        checkGoalInvitationStratus(goalInvitation, RequestStatus.REJECTED);
    }

    private GoalInvitation createTestGoalInvitation(){
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);

        return goalInvitation;
    }

    private void checkGoalInvitationStratus(GoalInvitation goalInvitation, RequestStatus status){
        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));

        GoalInvitationDto result = new GoalInvitationDto();

        switch (status){
            case ACCEPTED -> {
                doNothing().when(validation).checkAcceptingInvitation(goalInvitation.getId());
                result = goalInvitationService.acceptGoalInvitation(goalInvitation.getId());
            }
            case REJECTED -> {
                doNothing().when(validation).checkRejectingInvitation(goalInvitation.getId());
                result = goalInvitationService.rejectGoalInvitation(goalInvitation.getId());
            }
        }

        verify(goalInvitationRepository, times(1)).save(goalInvitation);

        assertEquals(status, result.getStatus());
    }




}
