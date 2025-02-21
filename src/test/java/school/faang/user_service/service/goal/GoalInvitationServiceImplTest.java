package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.filter.goal.invitation.GoalInvitationFilterDto;
import school.faang.user_service.filter.goal.invitation.GoalInvitationInviterIdFilter;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapperImpl;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.goal.GoalInvitationPrepareData.getActiveGoal;
import static school.faang.user_service.utils.goal.GoalInvitationPrepareData.getGoalInvitationDto;
import static school.faang.user_service.utils.goal.GoalInvitationPrepareData.getInvitationWithExistingGoal;
import static school.faang.user_service.utils.goal.GoalInvitationPrepareData.getInvitationWithNewGoal;
import static school.faang.user_service.utils.goal.GoalInvitationPrepareData.getInviterIdFilter;
import static school.faang.user_service.utils.goal.GoalInvitationPrepareData.getUser;
import static school.faang.user_service.utils.goal.GoalInvitationPrepareData.getUserWithAlreadyExistingGoal;
import static school.faang.user_service.utils.goal.GoalInvitationPrepareData.getUserWithMaxGoals;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceImplTest {
    private static final long INVITER_ID = 1L;
    private static final long INVITED_USER_ID = 2L;
    private static final long EXISTING_GOAL_ID = 1L;
    private static final long NEW_GOAL_ID = 3L;
    private static final long NEW_GOAL_INVITATION_ID = 1L;

    @Mock
    private GoalInvitationRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Spy
    private GoalInvitationMapperImpl mapper;

    private final List<Filter<GoalInvitation, GoalInvitationFilterDto>> invitationFilters = new ArrayList<>();

    private GoalInvitationServiceImpl service;

    @BeforeEach
    void init() {
        invitationFilters.add(new GoalInvitationInviterIdFilter());
        service = new GoalInvitationServiceImpl(repository, mapper, userRepository, goalRepository, invitationFilters);
    }

    @Test
    public void shouldCreateInvitationSuccessTest() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(repository.save(any())).thenReturn(any());

        service.createInvitation(getGoalInvitationDto());

        verify(repository).save(any());
    }

    @Test
    public void testCreateInvitationWithNotExistInviterUser() {
        when(userRepository.existsById(eq(INVITER_ID))).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> service.createInvitation(getGoalInvitationDto()));
    }

    @Test
    public void testCreateInvitationWithNotExistsInvitedUser() {
        when(userRepository.existsById(eq(INVITER_ID))).thenReturn(true);
        when(userRepository.existsById(eq(INVITED_USER_ID))).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> service.createInvitation(getGoalInvitationDto()));
    }

    @Test
    public void testCreateInvitationWithIdenticalUsers() {
        long sameUserIdForInviterAndInvited = 1L;
        when(userRepository.existsById(eq(sameUserIdForInviterAndInvited))).thenReturn(true);

        assertThrows(EntityNotFoundException.class,
                () -> service.createInvitation(getGoalInvitationDto()));
    }

    @Test
    public void testAcceptInvitationSuccess() {
        findInvitationByIdMockWithGoal(NEW_GOAL_ID);
        when(userRepository.findById(eq(INVITED_USER_ID))).thenReturn(Optional.ofNullable(getUser(INVITED_USER_ID)));
        when(goalRepository.existsById(eq(NEW_GOAL_ID))).thenReturn(true);
        when(goalRepository.findById(eq(NEW_GOAL_ID))).thenReturn(Optional.ofNullable(getActiveGoal(NEW_GOAL_ID)));
        when(goalRepository.save(any())).thenReturn(any());

        service.acceptGoalInvitation(NEW_GOAL_INVITATION_ID);

        verify(repository, times(2)).findById(anyLong());
    }

    @Test
    public void testAcceptInvitationWithMoreThanMaxGoals() {
        findInvitationByIdMockWithGoal(NEW_GOAL_ID);
        when(userRepository.findById(eq(INVITED_USER_ID))).thenReturn(Optional.ofNullable(getUserWithMaxGoals()));

        assertThrows(EntityNotFoundException.class,
                () -> service.acceptGoalInvitation(NEW_GOAL_INVITATION_ID));
    }

    @Test
    public void testAcceptInvitationWithAlreadyExistingGoal() {
        findInvitationByIdMockWithGoal(EXISTING_GOAL_ID);
        when(userRepository.findById(eq(INVITED_USER_ID)))
                .thenReturn(Optional.ofNullable(getUserWithAlreadyExistingGoal()));
        when(goalRepository.existsById(eq(EXISTING_GOAL_ID))).thenReturn(true);
        when(goalRepository.findById(eq(EXISTING_GOAL_ID)))
                .thenReturn(Optional.ofNullable(getActiveGoal(EXISTING_GOAL_ID)));

        assertThrows(EntityNotFoundException.class,
                () -> service.acceptGoalInvitation(NEW_GOAL_INVITATION_ID));
    }

    @Test
    public void testAcceptInvitationWithNotExistGoal() {
        findInvitationByIdMockWithGoal(NEW_GOAL_ID);
        when(userRepository.findById(eq(INVITED_USER_ID))).thenReturn(Optional.ofNullable(getUser(INVITED_USER_ID)));
        when(goalRepository.existsById(eq(NEW_GOAL_ID))).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> service.acceptGoalInvitation(NEW_GOAL_INVITATION_ID));
    }

    @Test
    public void testRejectInvitationSuccessTest() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(getInvitationWithNewGoal(RequestStatus.PENDING)));
        when(goalRepository.existsById(eq(NEW_GOAL_ID))).thenReturn(true);

        service.rejectGoalInvitation(NEW_GOAL_INVITATION_ID);

        verify(repository).save(getInvitationWithNewGoal(RequestStatus.REJECTED));
    }

    @Test
    public void testRejectInvitationWithNotExistGoalId() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(getInvitationWithNewGoal(RequestStatus.PENDING)));
        when(goalRepository.existsById(eq(NEW_GOAL_ID))).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> service.rejectGoalInvitation(NEW_GOAL_INVITATION_ID));
    }

    @Test
    public void testGetInvitationSuccessTestWithFilters() {
        when(repository.findAll()).thenReturn(List.of(getInvitationWithExistingGoal()));

        List<GoalInvitationDto> invitations = service.getInvitationsWithFilters(getInviterIdFilter());

        verify(repository).findAll();
        assertEquals(1, invitations.size());
    }

    private void findInvitationByIdMockWithGoal(long goalId) {
        Optional<GoalInvitation> goalInvitationEntity = Optional.of(
                mapper.toEntity(getGoalInvitationDto(NEW_GOAL_INVITATION_ID, INVITER_ID, INVITED_USER_ID, goalId))
        );
        when(repository.findById(eq(NEW_GOAL_INVITATION_ID)))
                .thenReturn(goalInvitationEntity);
    }
}