package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceImplTest {

    @Spy
    private GoalInvitationMapper mapper;
    @Mock
    private UserService userService;
    @Mock
    private GoalService goalService;
    @Mock
    private GoalInvitationRepository invitationRepository;
    @InjectMocks
    private GoalInvitationServiceImpl service;

    private GoalInvitationDto goalInvitationDto;
    private User user;
    private User anotherUser;
    private Goal goal;
    private GoalInvitation goalInvitation;
    private GoalInvitation completeGoalInvitation;
    private GoalInvitationDto completeGoalInvitationDto;

    @BeforeEach
    void setUp() {
        goalInvitationDto = GoalInvitationDto.builder()
                .inviterId(1L)
                .invitedId(2L)
                .goalId(1L)
                .build();

        user = new User();
        user.setId(goalInvitationDto.getInviterId());
        user.setUsername("username");

        anotherUser = new User();
        anotherUser.setId(goalInvitationDto.getInvitedId());
        anotherUser.setUsername("anotherUsername");

        goal = new Goal();
        goal.setId(goalInvitationDto.getGoalId());
        goal.setTitle("goal title");
        goal.setDescription("goal description");
        goal.setInvitations(new ArrayList<>());

        goalInvitation = new GoalInvitation();
        goalInvitation.setInviter(user);
        goalInvitation.setInvited(anotherUser);
        goalInvitation.setGoal(goal);
        goalInvitation.setStatus(RequestStatus.PENDING);

        completeGoalInvitation = new GoalInvitation();
        completeGoalInvitation.setId(1L);
        completeGoalInvitation.setInviter(user);
        completeGoalInvitation.setInvited(anotherUser);
        completeGoalInvitation.setGoal(goal);
        completeGoalInvitation.setStatus(RequestStatus.PENDING);
        completeGoalInvitation.setCreatedAt(LocalDateTime.now());
        completeGoalInvitation.setUpdatedAt(LocalDateTime.now());

        completeGoalInvitationDto = new GoalInvitationDto();
        completeGoalInvitationDto.setId(1L);
        completeGoalInvitationDto.setInviterId(user.getId());
        completeGoalInvitationDto.setInvitedId(anotherUser.getId());
        completeGoalInvitationDto.setGoalId(goal.getId());
    }

    @Test
    void testCreateInvitation_whenValidDtoPassed_thenReturnGoalInvitation() {
        when(userService.findById(goalInvitationDto.getInviterId())).thenReturn(user);
        when(userService.findById(goalInvitationDto.getInvitedId())).thenReturn(anotherUser);
        when(goalService.findById(goalInvitationDto.getGoalId())).thenReturn(goal);
        when(mapper.toGoalInvitation(user, anotherUser, goal)).thenReturn(goalInvitation);
        when(mapper.toGoalInvitationDto(completeGoalInvitation)).thenReturn(completeGoalInvitationDto);
        when(invitationRepository.save(any())).thenReturn(completeGoalInvitation);

        GoalInvitationDto savedInvitationDto = service.createInvitation(goalInvitationDto);

        assertEquals(savedInvitationDto, completeGoalInvitationDto);
        verify(invitationRepository, times(1)).save(any());
    }

    @Test
    void testCreateInvitation_whenInviterAndInvitedAreTheSame_thenThrowIllegalArgumentException() {
        when(userService.findById(goalInvitationDto.getInviterId())).thenReturn(user);
        when(userService.findById(goalInvitationDto.getInvitedId())).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> service.createInvitation(goalInvitationDto));
        verify(invitationRepository, times(0)).save(any());
    }

    @Test
    void testCreateInvitation_whenGoalIsNotFound_thenThrowIllegalArgumentException() {
        when(userService.findById(goalInvitationDto.getInviterId())).thenReturn(user);
        when(userService.findById(goalInvitationDto.getInvitedId())).thenReturn(anotherUser);
        when(goalService.findById(goalInvitationDto.getGoalId())).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> service.createInvitation(goalInvitationDto));
        verify(invitationRepository, times(0)).save(any());
    }
}