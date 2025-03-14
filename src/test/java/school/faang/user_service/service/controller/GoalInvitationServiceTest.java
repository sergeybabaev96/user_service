package school.faang.user_service.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.goal.GoalInvitationConfig;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.InvalidInvitationException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private GoalService goalService;

    @Mock
    private UserService userService;

    @Mock
    private GoalInvitationMapper goalInvitationMapper;

    @Mock
    private GoalInvitationConfig goalInvitationConfig;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    private GoalInvitationDto invitationDto;
    private GoalInvitation invitation;

    private static final long INVITATION_ID = 1L;
    private static final long INVITER_ID = 1L;
    private static final long INVITED_USER_ID = 2L;
    private static final long GOAL_ID = 1L;
    private static final int MAX_ACTIVE_GOALS = 5;
    private static final long ACTIVE_GOALS_COUNT = 4L;

    @BeforeEach
    void setUp() {
        invitationDto = new GoalInvitationDto(
                INVITATION_ID,
                INVITER_ID,
                INVITED_USER_ID,
                GOAL_ID,
                RequestStatus.PENDING
        );

        User inviter = new User();
        inviter.setId(INVITER_ID);

        User invited = new User();
        invited.setId(INVITED_USER_ID);

        Goal goal = new Goal();
        goal.setId(GOAL_ID);

        invitation = new GoalInvitation();
        invitation.setId(INVITATION_ID);
        invitation.setStatus(RequestStatus.PENDING);
        invitation.setInviter(inviter);
        invitation.setInvited(invited);
        invitation.setGoal(goal);
    }

    @Test
    void testCreateInvitation() {
        when(goalInvitationMapper.toEntity(invitationDto)).thenReturn(invitation);
        when(goalService.existsById(GOAL_ID)).thenReturn(true);
        doNothing().when(userService).checkUserExists(INVITER_ID);
        doNothing().when(userService).checkUserExists(INVITED_USER_ID);
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(invitation);

        goalInvitationService.createInvitation(invitationDto);

        ArgumentCaptor<GoalInvitation> captor = ArgumentCaptor.forClass(GoalInvitation.class);
        verify(goalInvitationRepository).save(captor.capture());
        assertEquals(RequestStatus.PENDING, captor.getValue().getStatus());
    }

    @Test
    void testCreateInvitation_InvalidInviterAndInvited() {
        invitation.setInviter(null);
        invitation.setInvited(null);

        when(goalInvitationMapper.toEntity(invitationDto)).thenReturn(invitation);

        InvalidInvitationException exception = assertThrows(InvalidInvitationException.class, () ->
                goalInvitationService.createInvitation(invitationDto)
        );
        assertEquals("Inviter and invited user must be specified.", exception.getMessage());
    }

    @Test
    void testCreateInvitation_SameInviterAndInvited() {
        invitation.getInvited().setId(INVITER_ID);

        when(goalInvitationMapper.toEntity(invitationDto)).thenReturn(invitation);

        InvalidInvitationException exception = assertThrows(InvalidInvitationException.class, () ->
                goalInvitationService.createInvitation(invitationDto)
        );
        assertEquals("Inviter and invited user cannot be the same.", exception.getMessage());
    }

    @Test
    void testCreateInvitation_InvalidGoal() {
        when(goalInvitationMapper.toEntity(invitationDto)).thenReturn(invitation);
        when(goalService.existsById(GOAL_ID)).thenReturn(false);

        InvalidInvitationException exception = assertThrows(InvalidInvitationException.class, () ->
                goalInvitationService.createInvitation(invitationDto)
        );
        assertEquals("Goal does not exist.", exception.getMessage());
    }

    @Test
    void testAcceptGoalInvitation() {
        when(goalInvitationRepository.findById(INVITATION_ID)).thenReturn(Optional.of(invitation));
        when(goalInvitationConfig.getMaxActiveGoals()).thenReturn(MAX_ACTIVE_GOALS);
        when(goalService.countActiveGoalsPerUser(INVITED_USER_ID)).thenReturn(ACTIVE_GOALS_COUNT);

        goalInvitationService.acceptGoalInvitation(INVITATION_ID);

        assertEquals(RequestStatus.ACCEPTED, invitation.getStatus());
        verify(goalInvitationRepository).save(invitation);
    }

    @Test
    void testAcceptGoalInvitation_AlreadyProcessed() {
        invitation.setStatus(RequestStatus.ACCEPTED);

        when(goalInvitationRepository.findById(INVITATION_ID)).thenReturn(Optional.of(invitation));

        InvalidInvitationException exception = assertThrows(InvalidInvitationException.class, () ->
                goalInvitationService.acceptGoalInvitation(INVITATION_ID)
        );
        assertEquals("Invitation is already processed.", exception.getMessage());
    }

    @Test
    void testAcceptGoalInvitation_MaxActiveGoals() {
        when(goalInvitationRepository.findById(INVITATION_ID)).thenReturn(Optional.of(invitation));
        when(goalInvitationConfig.getMaxActiveGoals()).thenReturn(MAX_ACTIVE_GOALS);
        when(goalService.countActiveGoalsPerUser(INVITED_USER_ID)).thenReturn((long) MAX_ACTIVE_GOALS);

        InvalidInvitationException exception = assertThrows(InvalidInvitationException.class, () ->
                goalInvitationService.acceptGoalInvitation(INVITATION_ID)
        );
        assertEquals("User has reached the maximum number of active goals.", exception.getMessage());
    }

    @Test
    void testRejectGoalInvitation() {
        when(goalInvitationRepository.findById(INVITATION_ID)).thenReturn(Optional.of(invitation));

        goalInvitationService.rejectGoalInvitation(INVITATION_ID);

        assertEquals(RequestStatus.REJECTED, invitation.getStatus());
        verify(goalInvitationRepository).save(invitation);
    }

    @Test
    void testRejectGoalInvitation_AlreadyProcessed() {
        invitation.setStatus(RequestStatus.ACCEPTED);

        when(goalInvitationRepository.findById(INVITATION_ID)).thenReturn(Optional.of(invitation));

        InvalidInvitationException exception = assertThrows(InvalidInvitationException.class, () ->
                goalInvitationService.rejectGoalInvitation(INVITATION_ID)
        );
        assertEquals("Invitation is already processed.", exception.getMessage());
    }

    @Test
    void testGetInvitations() {
        InvitationFilterDto filter = new InvitationFilterDto(INVITER_ID, INVITED_USER_ID, RequestStatus.PENDING);
        when(goalInvitationRepository.findByInviterIdAndInvitedIdAndStatus(INVITER_ID, INVITED_USER_ID, RequestStatus.PENDING))
                .thenReturn(Collections.singletonList(invitation));
        when(goalInvitationMapper.toDto(invitation)).thenReturn(invitationDto);

        List<GoalInvitationDto> result = goalInvitationService.getInvitations(filter);

        assertEquals(1, result.size());
        assertEquals(invitationDto, result.get(0));
    }

    @Test
    void testGetInvitationsByInvitedUserId() {
        when(goalInvitationRepository.findByInvitedId(INVITED_USER_ID)).thenReturn(Collections.singletonList(invitation));
        when(goalInvitationMapper.toDto(invitation)).thenReturn(invitationDto);

        List<GoalInvitationDto> result = goalInvitationService.getInvitationsByInvitedUserId(INVITED_USER_ID);

        assertEquals(1, result.size());
        assertEquals(invitationDto, result.get(0));
    }

    @Test
    void testGetInvitationById_NotFound() {
        when(goalInvitationRepository.findById(INVITATION_ID)).thenReturn(Optional.empty());

        InvalidInvitationException exception = assertThrows(InvalidInvitationException.class, () ->
                goalInvitationService.getInvitationById(INVITATION_ID)
        );
        assertEquals("Invitation not found.", exception.getMessage());
    }
}
